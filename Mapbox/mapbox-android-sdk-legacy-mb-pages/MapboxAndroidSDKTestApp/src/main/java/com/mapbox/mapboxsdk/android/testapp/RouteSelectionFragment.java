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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String  RoutesasString = "routes";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private JSONArray routesArray;
    private ArrayList<JSONObject> jsonObjectRoutes;
    public NavigationMap navMap;

    private OnFragmentInteractionListener mListener;

    public RouteSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteSelectionFragment newInstance(String param1, String param2) {
        RouteSelectionFragment fragment = new RouteSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static RouteSelectionFragment createInstance(String routes) {
        RouteSelectionFragment newFragment = new RouteSelectionFragment();
        Bundle args = new Bundle();
        args.putString(RoutesasString, routes);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            //View navSearchBarHandle = findViewById(R.id.navAddressBar);
            //navAddressBarHandle.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_selection, container, false);

        navMap = (NavigationMap) getFragmentManager().findFragmentByTag("navMapTag");

        try {
            routesArray = new JSONArray(getArguments().getString(RoutesasString));//obj.getJSONArray("routes");
        } catch (Exception ex) {
            //Log.i(TAG, "Error getting json in NavRouteFrag.");
            //Log.i(TAG, ex.getMessage());
        }

        // Add all the LineStrings for the routes to our private field
        jsonObjectRoutes = new ArrayList<JSONObject>();
        for (int ii = 0; ii < routesArray.length(); ii++) {
            try {
                jsonObjectRoutes.add(routesArray.getJSONObject(ii));
                //JSONObject routeAsJson = jsonRoutesArray.getJSONObject(ii);
                //routesAsLineStrings.add((LineString) GeoJSON.parse(routeAsJson.getJSONObject("geometry")));
            } catch (Exception ex) {
                String exMessage = ex.getMessage();
                //Log.i(TAG, "Exception in onCreateView() for NavigatinRoutesFragment, iteration " + ii);
                //Log.i(TAG, exMessage);
            }
        }

        // Configure the adapter
        ListView routesListView = (ListView) view.findViewById(R.id.navigation_routesList);
        routesListView.setAdapter(new RoutesListAdapter(getActivity().getApplicationContext(),
                R.layout.fragment_route_selection_single, jsonObjectRoutes));


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



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
                    } catch (Exception ex) {
                        //Log.i(TAG, "Error in onClick for item in pos " + position + " in NavRouteFrag.");
                        //Log.i(TAG, ex.getMessage());

                    }
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
                //Log.i(TAG, "Error getting dist for item in pos " + position + " in NavRouteFrag.");
               // Log.i(TAG, ex.getMessage());
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
