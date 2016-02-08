package com.mapbox.mapboxsdk.android.testapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.LineString;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RouteSelectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RouteSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RouteSelectionFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String  RoutesasString = "routes";

    private String mParam1;
    private String mParam2;
    private JSONArray routesArray;
    private ArrayList<JSONObject> jsonObjectRoutes;
    public NavigationMap navMap;

    private OnFragmentInteractionListener mListener;

    // Required empty public constructor
    public RouteSelectionFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * 
     * @return A new instance of fragment NavigationFragment.
     */
    public static RouteSelectionFragment newInstance(String param1, String param2) {
        RouteSelectionFragment fragment = new RouteSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_selection, container, false);

        navMap = (NavigationMap) getFragmentManager().findFragmentByTag("navMapTag");

        try {
            routesArray = new JSONArray(getArguments().getString(RoutesasString));
        } catch (Exception ex) {}

        // Add all the LineStrings for the routes to our private field
        jsonObjectRoutes = new ArrayList<JSONObject>();
        for (int ii = 0; ii < routesArray.length(); ii++) {
            try {
                jsonObjectRoutes.add(routesArray.getJSONObject(ii));
            } catch (Exception ex) {}
        }

        // Configure the adapter
        ListView routesListView = (ListView) view.findViewById(R.id.navigation_routesList);
        routesListView.setAdapter(new RoutesListAdapter(getActivity().getApplicationContext(),
                R.layout.fragment_route_selection_single, jsonObjectRoutes));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //holds the route
    private class RoutesListAdapter extends ArrayAdapter<JSONObject> {
        private int resource;
        private RoutesListAdapter(Context context, int resource, List<JSONObject> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(resource, parent, false);
            RouteInformationObject routeInfoObj = new RouteInformationObject();

            // Get UI elements
            routeInfoObj.selectionButton = (Button) convertView.findViewById(R.id.navigation_selectButton);
            routeInfoObj.routeId = (TextView) convertView.findViewById(R.id.navigation_routeId);
            routeInfoObj.routeDistanceInMiles = (TextView) convertView.findViewById(R.id.navigation_milesToDestination);
            final JSONObject thisRoute = getItem(position);

            // Configure button
            routeInfoObj.selectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject tempGeo = thisRoute.getJSONObject("geometry");
                        LineString tempLS = (LineString) GeoJSON.parse(tempGeo);
                        String stop = "stop";
                        navMap.displayRoute = tempLS;
                        getFragmentManager().popBackStackImmediate();
                    } catch (Exception ex) {}
                }
            });

            // Configure routeId
            routeInfoObj.routeId.setText("Route " + (position + 1));

            // Configure routeDistance
            try {
                double milesPerMeter = 0.000621371;
                double miles = thisRoute.getDouble("distance") * milesPerMeter;
                DecimalFormat decFormat = new DecimalFormat("#.#");
                routeInfoObj.routeDistanceInMiles.setText(decFormat.format(miles) + " miles");
            } catch (Exception ex) {
                routeInfoObj.routeDistanceInMiles.setText("Error");
            }

            return convertView;
        }
    }

    private class RouteInformationObject {
        Button selectionButton;
        TextView routeId;
        TextView routeDistanceInMiles;
    }
}
