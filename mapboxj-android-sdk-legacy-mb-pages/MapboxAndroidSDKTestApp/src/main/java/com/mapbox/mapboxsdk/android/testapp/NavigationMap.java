package com.mapbox.mapboxsdk.android.testapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.Toast;

import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.Position;
import com.cocoahero.android.geojson.LineString;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.overlay.PathOverlay;


import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationMap.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationMap extends Fragment {
    private static final String ARG_PARAM1 = "Latitude";
    private static final String ARG_PARAM2 = "Longitude";

    private Double Latitude;
    private Double Longitude;
    private Double currentLat;
    private Double currentLong;
    private MapView mv;
    public LineString  displayRoute = null;

    public JSONObject navJSON;

    private OnFragmentInteractionListener mListener;

    // Required empty public constructor
    public NavigationMap() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Latitude = getArguments().getDouble(ARG_PARAM1);
            Longitude = getArguments().getDouble(ARG_PARAM2);
        }
        currentLat = 40.1591525;
        currentLong = -74.0277742;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_map, container, false);

        //prepare map
        mv = (MapView) view.findViewById(R.id.navigationMapView);
        mv.setCenter(new LatLng(Latitude, Longitude));
        mv.setZoom(14);

        //getting addresses
        Context ourContext = this.getActivity();
        List<Address> addresses = MainActivity.getAddressfromLocation(ourContext, new LatLng(Latitude, Longitude));
        Address currentAddress = addresses.get(0);

        //add marker
        Marker cap = new Marker(mv, currentAddress.getAddressLine(0), currentAddress.getAddressLine(1), new LatLng(Latitude, Longitude));
        cap.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "circle-stroked", "FF0000"));
        cap.setToolTip(new NavCustomInfoWindow( mv, this, getFragmentManager() ) );
        mv.addMarker(cap);

        //route for navigation
        if (displayRoute != null) {
            OverlayRouteFromGeoJsonLineString(displayRoute);
        }

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

    //sets navigation route on map
    public void OverlayRouteFromGeoJsonLineString(LineString  routeAsGeoJsonLineString) {

        try {
            // Turn it into an array of LatLngs
            List<Position> routeAsPositions = routeAsGeoJsonLineString.getPositions();
            ArrayList<LatLng> routeAsLatLngs = new ArrayList();
            for (int i = 0; i < routeAsPositions.size(); i++) {
                routeAsLatLngs.add(new LatLng(routeAsPositions.get(i).getLatitude(), routeAsPositions.get(i).getLongitude()));
            }

            // Overlay it as a path
            PathOverlay po = new PathOverlay();
            po.addPoints(routeAsLatLngs);
            mv.addOverlay(po);

        } catch (Exception ex) {}
    }

    /**
     * custom marker class
     */
    public class NavCustomInfoWindow extends InfoWindow {
        MapView mapview;
        NavigationMap thisNavMap = null;
        private FragmentManager fragManager;
        String streetInfo = "";
        String stateInfo = "";

        public NavCustomInfoWindow(final MapView mv, NavigationMap argNavMap, FragmentManager fragmentManager) {
            super(R.layout.infowindow_custom, mv);
            thisNavMap = argNavMap;
            fragManager = fragmentManager;
        }


//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent event) {
//
//            return true;
//        }
//
//        @Override
//        public boolean onDoubleTap(MotionEvent event) {
//            Fragment sendfrag = new SendFragment();
//            Bundle args = new Bundle();
//            args.putString("Street Info", streetInfo);
//            args.putString("State Info", stateInfo);
//
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.replace(((ViewGroup)getView().getParent()).getId(), sendfrag );
//                    transaction.commit();
//
//            return true;
//        }
//
//        @Override
//        public boolean onDoubleTapEvent(MotionEvent event) {
//            Fragment sendfrag = new SendFragment();
//            Bundle args = new Bundle();
//            args.putString("Street Info", streetInfo);
//            args.putString("State Info", stateInfo);
//
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.replace(((ViewGroup)getView().getParent()).getId(), sendfrag );
//            transaction.commit();
//            return true;
//        }

        /**
         * Dynamically set the content in the CustomInfoWindow
         *
         * @param overlayItem The tapped Marker
         */
        @Override
        public void onOpen(final Marker overlayItem) {

            //Set Street info
            streetInfo = overlayItem.getTitle();
            ((TextView) mView.findViewById(R.id.customInfo_Street)).setText(streetInfo);

            //Set State, country, zip info
            stateInfo = overlayItem.getDescription();
            ((TextView) mView.findViewById(R.id.customInfo_State)).setText(stateInfo);

            // Add own OnTouchListener to customize handling InfoWindow touch events
            TextView linkTextBox = ((TextView) mView.findViewById(R.id.customInfo_NavLink));


            //When InfoWindow is long touched, move to SendFragment with info window information
            setOnLongTouchListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {

                   //Lets us know that the touch was performed
                   Toast.makeText(getActivity().getApplicationContext(), "Long Touched", Toast.LENGTH_SHORT).show();

                   //Create and populate a data bundle to send to the new fragment
                    Fragment sendfrag = new SendFragment();
                    Bundle args = new Bundle();
                    args.putString("Address", streetInfo);
                    args.putString("State Info", stateInfo);
                    sendfrag.setArguments(args);

                   //Switch Fragments
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                            //transaction.replace(R.id.navigationMapView, sendfrag );
                            transaction.replace(R.id.content_frame, sendfrag );
                            transaction.commit();

                   return true;
               }
            });


            linkTextBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mapview = mv;

                    mapview.setZoom(6);

                    // make a new marker at our "Current location", being the University of Cincinnati
                    Marker cap = new Marker(mapview, "Current Location", "", new LatLng(currentLat, currentLong));
                    cap.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "circle-stroked", "FF0000"));
                    mapview.addMarker(cap);

                    //Form directions query
                    String waypointString = overlayItem.getPosition().getLongitude() + "," + overlayItem.getPosition().getLatitude() + ";"
                            + currentLong + "," + currentLat;
                            String query = getString(R.string.directionQuery1) + waypointString + getString(R.string.directionQuery2);

                            //set up parameters for AsyncTask

                            JsonURLReader currJsonURLReader = new JsonURLReader();
                            currJsonURLReader.execute(query);

                            //Draw from markers to waypoints on map.
                            return true;
                        }
                    });

        }

        //Adapted from code by John Mikolay

        class JsonURLReader extends AsyncTask<String, Void , JSONObject> {

            protected JSONObject doInBackground(String... params) {

                try {
                    JSONObject routesObject = DataLoadingUtils.loadJSONFromUrl(params[0]);
                    return routesObject;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(JSONObject currJSON) {
                JSONArray routesArray = null;
                try {
                    routesArray = currJSON.getJSONArray("routes");
                } catch (Exception e) {}

                if (routesArray.length() < 1) {}
                else if  (routesArray.length() == 1) {

                    try {
                        JSONObject firstRouteAsJson = routesArray.getJSONObject(0);
                        LineString firstRouteAsLineString = (LineString) GeoJSON.parse(firstRouteAsJson.getJSONObject("geometry"));
                        thisNavMap.OverlayRouteFromGeoJsonLineString(firstRouteAsLineString);
                    }catch (Exception e) {}
                }

                else {

                    try {
                        // Create the new navigation routes fragment and give it the navigation fragment
                        RouteSelectionFragment newNavRoutesFrag = RouteSelectionFragment.createInstance(routesArray.toString());

                        FragmentTransaction transaction = fragManager.beginTransaction();
                        transaction.replace(R.id.content_frame, newNavRoutesFrag);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }catch (Exception e) {}
                }
            }
        }
    }
}

class TaskParameters{
    StringBuffer data;
    String url = null;
    TextView JSONStorage;
    
    public TaskParameters(StringBuffer argData,String argURL){
        data = argData;
        url = argURL;
    }
}
