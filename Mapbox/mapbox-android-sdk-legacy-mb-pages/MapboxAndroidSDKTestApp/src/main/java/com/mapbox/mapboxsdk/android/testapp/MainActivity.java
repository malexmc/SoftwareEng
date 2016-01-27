package com.mapbox.mapboxsdk.android.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.ITileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.TilesLoadedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	static final int QUERY_REQUEST = 19309;

	private DrawerLayout          mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavigationView        mNavigationView;
	private Menu                  testFragmentNames;
	private int selectedFragmentIndex = 0;
	private MapView mapView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		   MapView.setDebugMode(true); //make sure to call this before the view is created!
           */
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
		mNavigationView.setNavigationItemSelectedListener(this);

		// Set the adapter for the list view
		testFragmentNames = mNavigationView.getMenu();
		int i = 0;
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.navigation));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.mainTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.alternateTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.markersTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.itemizedOverlayTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.localGeoJSONTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.localOSMTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.diskCacheDisabledTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.offlineCacheTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.programmaticTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.webSourceTileTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.locateMeTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.pathTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.bingTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.saveMapOfflineTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.tapForUTFGridTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.customMarkerTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.rotatedMapTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.clusteredMarkersTestMap));
		testFragmentNames.add(Menu.NONE, i++, Menu.NONE, getString(R.string.mbTilesTestMap));
		testFragmentNames.add(Menu.NONE, i, Menu.NONE, getString(R.string.draggableMarkersTestMap));


		// Set the drawer toggle as the DrawerListener
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigationdrawer_open, R.string.navigationdrawer_close);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		setSupportActionBar(toolbar);

		// Set MainTestFragment
		selectItem(0);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected(item);
	}

	/**
	 * Swaps fragments in the main content view
	 */
	private void selectItem(int position) {
		final MenuItem menuItem = mNavigationView.getMenu().findItem(position);
		setTitle(menuItem.getTitle());

		selectedFragmentIndex = position;
		// Create a new fragment and specify the planet to show based on position
		Fragment fragment;

		switch (position) {
			case 0:
				fragment = new NavigationFragment();
				break;
			case 1:
				fragment = new MainTestFragment();
				break;
			case 2:
				fragment = new AlternateMapTestFragment();
				break;
			case 3:
				fragment = new MarkersTestFragment();
				break;
			case 4:
				fragment = new ItemizedIconOverlayTestFragment();
				break;
			case 5:
				fragment = new LocalGeoJSONTestFragment();
				break;
			case 6:
				fragment = new LocalOSMTestFragment();
				break;
			case 7:
				fragment = new DiskCacheDisabledTestFragment();
				break;
			case 8:
				fragment = new OfflineCacheTestFragment();
				break;
			case 9:
				fragment = new ProgrammaticTestFragment();
				break;
			case 10:
				fragment = new WebSourceTileTestFragment();
				break;
			case 11:
				fragment = new LocateMeTestFragment();
				break;
			case 12:
				fragment = new PathTestFragment();
				break;
			case 13:
				fragment = new BingTileTestFragment();
				break;
			case 14:
				fragment = new SaveMapOfflineTestFragment();
				break;
			case 15:
				fragment = new TapForUTFGridTestFragment();
				break;
			case 16:
				fragment = new CustomMarkerTestFragment();
				break;
			case 17:
				fragment = new RotatedMapTestFragment();
				break;
			case 18:
				fragment = new ClusteredMarkersTestFragment();
				break;
			case 19:
				fragment = new MBTilesTestFragment();
				break;
			case 20:
				fragment = new DraggableMarkersTestFragment();
				break;
			default:
				fragment = new MainTestFragment();
				break;
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commit();

		mDrawerLayout.closeDrawer(mNavigationView);
	}

	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}

	@Override
	public boolean onNavigationItemSelected(final MenuItem menuItem) {
		selectItem(menuItem.getItemId());
		return true;
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	public void onAddrClick(View v) {
		String query = getString(R.string.query1) + "1600+pennsylvania+ave+nw" + getString(R.string.query2) + getString(R.string.testAccessToken );
		String address = "1600pennsylvaniaAvenue";

		Context radda = MainActivity.this.getApplicationContext();
		LatLng currentLatLng = new LatLng(0,0);
		try {

			currentLatLng = getLocationFromAddress(radda, address);
			String stop = "stop";

		} catch(Exception e) {}

		//super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/** Create a mapView and give it some properties */
		mapView = (MapView) findViewById(R.id.mapview);
		//mapView.setStyleUrl(Style.MAPBOX_STREETS);
		//mapView.setCenterCoordinate(currentLatLng);
		//mapView.setZoomLevel(11);


		mapView.setVisibility(View.VISIBLE);
	}

	public LatLng getLocationFromAddress(Context context, String strAddress) {

		Geocoder coder = new Geocoder(context);
		List<Address> address;
		LatLng p1 = null;

		try {
			address = coder.getFromLocationName(strAddress, 5);
			if (address == null) {
				return null;
			}
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();

			p1 = new LatLng(location.getLatitude(), location.getLongitude() );
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return p1;
	}


}