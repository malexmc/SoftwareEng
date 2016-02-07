package com.mapbox.mapboxsdk.android.testapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.mapbox.mapboxsdk.util.NetworkUtils;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.LineString;
import com.cocoahero.android.geojson.Position;
import com.mapbox.mapboxsdk.android.testapp.ui.CustomInfoWindow;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationMap.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationMap extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Latitude";
    private static final String ARG_PARAM2 = "Longitude";

    // TODO: Rename and change types of parameters
    private Double Latitude;
    private Double Longitude;
    private Double currentLat;
    private Double currentLong;
    private MapView mv;
    public LineString  displayRoute = null;

    public JSONObject navJSON;

    private OnFragmentInteractionListener mListener;

    public NavigationMap() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param Latitude  Parameter 1.
     * @param Longitude Parameter 2.
     * @return A new instance of fragment NavigationMap.
     */
    // TODO: Rename and change types and number of parameters
//    public static NavigationMap newInstance(Integer localLatitude, Integer localLongitude) {
//        NavigationMap fragment = new NavigationMap();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, localLatitude);
//        args.putString(ARG_PARAM2, localLongitude);
//        fragment.setArguments(args);
//        return fragment;
//    }
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

        mv = (MapView) view.findViewById(R.id.navigationMapView);
        mv.setCenter(new LatLng(Latitude, Longitude));
        mv.setZoom(14);

        Context ourContext = this.getActivity();
        List<Address> addresses = getAddressfromLocation(ourContext, new LatLng(Latitude, Longitude));
        Address currentAddress = addresses.get(0);

        Marker cap = new Marker(mv, currentAddress.getAddressLine(0), currentAddress.getAddressLine(1), new LatLng(Latitude, Longitude));
        cap.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "town-hall", "FF0000"));
        cap.setToolTip(new NavCustomInfoWindow( mv, this, getFragmentManager() ) );
        mv.addMarker(cap);

        if (displayRoute != null) {
            OverlayRouteFromGeoJsonLineString(displayRoute);
        }

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

    public List<Address> getAddressfromLocation(Context context, LatLng paramLatLng) {

        Geocoder coder = new Geocoder(context);
        List<Address> address = null;
        LatLng p1 = null;

        try {
            address = coder.getFromLocation(paramLatLng.getLatitude(), paramLatLng.getLongitude(), 1);
            if (address == null) {
                return null;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return address;
    }

    public void OverlayRouteFromGeoJsonLineString(LineString  routeAsGeoJsonLineString) {

        try {
            // Get the first json route (our primary route)
            //LineString firstRouteAsLineString = (LineString) GeoJSON.parse(new JSONObject(routeAsGeoJsonLineString));

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

        } catch (Exception ex) {
//            String exMessage = ex.getMessage();
//            Log.i(TAG, "Exception in OverlayRouteFromGeoJsonLineString()");
//            Log.i(TAG, exMessage);
//            Toast.makeText(currentView.getContext(), "Could not overlay route.", Toast.LENGTH_SHORT).show();
        }
    }

    public class NavCustomInfoWindow extends InfoWindow {
        MapView mapview;
        NavigationMap thisNavMap = null;
        private FragmentManager fragManager;

        public NavCustomInfoWindow(final MapView mv, NavigationMap argNavMap, FragmentManager fragmentManager) {
            super(R.layout.infowindow_custom, mv);
            thisNavMap = argNavMap;
            fragManager = fragmentManager;

            // Add own OnTouchListener to customize handling InfoWindow touch events
//            TextView linkTextBox = ((TextView) mView.findViewById(R.id.customInfo_Street));
//            linkTextBox.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    mapview = mv;
//
//                    mapview.setZoom(6);
//
//                    // make a new marker at our "Current location", being the University of Cincinnati
//                    Marker cap = new Marker(mapview, "Current Location", "", new LatLng(currentLat, currentLong));
//                    cap.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "town-hall", "FF0000"));
//                    mapview.addMarker(cap);
//
//
//                    return true;
//                }
//            });


        }

        /**
         * Dynamically set the content in the CustomInfoWindow
         *
         * @param overlayItem The tapped Marker
         */
        @Override
        public void onOpen(final Marker overlayItem) {
            //Set Street info
            String streetString = overlayItem.getTitle();
            ((TextView) mView.findViewById(R.id.customInfo_Street)).setText(streetString);

            //Set State, country, zip info
            String stateString = overlayItem.getDescription();
            ((TextView) mView.findViewById(R.id.customInfo_State)).setText(stateString);


            // Add own OnTouchListener to customize handling InfoWindow touch events
            TextView linkTextBox = ((TextView) mView.findViewById(R.id.customInfo_NavLink));
            linkTextBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mapview = mv;

                    mapview.setZoom(6);

                    // make a new marker at our "Current location", being the University of Cincinnati
                    Marker cap = new Marker(mapview, "Current Location", "", new LatLng(currentLat, currentLong));
                    cap.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "town-hall", "FF0000"));
                    mapview.addMarker(cap);

                    //Form directions query
                    String waypointString = overlayItem.getPosition().getLongitude()+","+ overlayItem.getPosition().getLatitude()+ ";"
                            +currentLong +"," + currentLat;
                    String query = getString(R.string.directionQuery1) + waypointString + getString(R.string.directionQuery2);

                    //set up parameters for AsyncTask

                    JsonURLReader currJsonURLReader = new JsonURLReader();
                    currJsonURLReader.execute(query);
                    //Run AsyncTask
                    //jsonURLReader reader = new jsonURLReader(getActivity());


//                    try {
//                        reader.execute();//.get();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

//                    try {
//                        jsonURLReader json = new jsonURLReader();
//                    }
//                    catch (Exception e) {
//                        return false;
//                    }
                    //Get Waypoints from object

                    //Draw from markers to waypoints on map.
                    return true;
                }
            });

        }

//    public class JsonReader {
//
//        private String readAll(Reader rd) throws IOException {
//            StringBuilder sb = new StringBuilder();
//            int cp;
//            while ((cp = rd.read()) != -1) {
//                sb.append((char) cp);
//            }
//            return sb.toString();
//        }
//
//        public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
//            URL jsonPage = new URL(url);
//            InputStream is = jsonPage.openStream();
//            try {
//                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//                String jsonText = readAll(rd);
//                JSONObject json = new JSONObject(jsonText);
//                return json;
//            } finally {
//                is.close();
//            }
//        }
//
//        public void main(String[] args) throws IOException, JSONException {
//            jsonURLReader json = new jsonURLReader("https://graph.facebook.com/19292868552");
//        }
//    }

        //Adapted from code by John Mikolay


        class JsonURLReader extends AsyncTask<String, Void , JSONObject> {
            JSONObject[] json = null;



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


            private String readAll(Reader rd) throws IOException {
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                return sb.toString();
            }


            protected void onPostExecute(JSONObject currJSON) {
                JSONArray routesArray = null;
                try {
                    routesArray = currJSON.getJSONArray("routes");
                } catch (Exception e) {
                    //Toast.makeText(mView.getContext(), "Could not get routes.", Toast.LENGTH_SHORT).show();
                }

                if (routesArray.length() < 1) {

                }

                else if  (routesArray.length() == 1) {

                    try {
                        JSONObject firstRouteAsJson = routesArray.getJSONObject(0);
                        LineString firstRouteAsLineString = (LineString) GeoJSON.parse(firstRouteAsJson.getJSONObject("geometry"));
                        thisNavMap.OverlayRouteFromGeoJsonLineString(firstRouteAsLineString);
                    }

                    catch (Exception e) {
                    }
                }

                else {

                    try {
                        // Create the new navigation routes fragment and give it the navigation fragment
                        RouteSelectionFragment newNavRoutesFrag = RouteSelectionFragment.createInstance(routesArray.toString());// new NavigationRoutesFragment();
                        //newNavRoutesFrag.navFragment = owningNavFragment;
                        //newNavRoutesFrag.jsonRoutesArray = jsonRoutesArray;

                        FragmentTransaction transaction = fragManager.beginTransaction();
                        transaction.replace(R.id.content_frame, newNavRoutesFrag);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                    catch (Exception e) {
                    }
                }
            }
        }

    }
}








    class TaskParameters {
        StringBuffer data;
        String url = null;
        TextView JSONStorage;

        public TaskParameters(StringBuffer argData, String argURL) {
            data = argData;
            url = argURL;
        }
    }
