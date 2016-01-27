package com.mapbox.mapboxsdk.android.testapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cocoahero.android.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment {

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        //mapView = (MapView) view.findViewById(R.id.localGeoJSONMapView);
        //mapView.setCenter(new LatLng(47.668780,-122.387883));
        //mapView.setZoom(14);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load GeoJSON
        try {
            FeatureCollection features = DataLoadingUtils.loadGeoJSONFromAssets(getActivity(), "spatialdev_small.geojson");
            ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(features, null);

            for (Object obj : uiObjects) {
                if (obj instanceof Marker) {
                    //mapView.addMarker((Marker) obj);
                } else if (obj instanceof PathOverlay) {
                    //mapView.getOverlays().add((PathOverlay) obj);
                }
            }
            if (uiObjects.size() > 0) {
               // mapView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
