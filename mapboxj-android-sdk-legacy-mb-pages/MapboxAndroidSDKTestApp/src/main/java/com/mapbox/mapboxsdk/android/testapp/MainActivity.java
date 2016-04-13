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
import android.widget.EditText;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Signature;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	private DrawerLayout          mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavigationView        mNavigationView;
	private Menu                  testFragmentNames;
	private String				  mapboxKey = "IAMMAPBOX";
	private int selectedFragmentIndex = 0;

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

	public void addToContact(View V) {
		//Set intent action as ACTION_SEND
		Intent intent = new Intent("ADD_CONTACT");
		String title = "Add to";


		////////////////////////
		KeyPairGenerator kpg = null;
		byte[] data = null;
		byte[] signatureBytes = null;
		Signature sig = null;
		KeyPair keyPair = null;

		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			keyPair = kpg.genKeyPair();
			data = mapboxKey.getBytes("UTF8");
			sig = Signature.getInstance("MD5WithRSA");
			sig.initSign(keyPair.getPrivate());
			sig.update(data);
			signatureBytes = sig.sign();
			//sig.initVerify(keyPair.getPublic());
			//	sig.update(data);

			//sig.

			//sig.initVerify(keyPair.getPublic());
			//sig.update(data);
		}
		catch(Exception e){}
		//////////////////////////

		KeyInfo currentKeyInfo = new KeyInfo(keyPair.getPublic(), mapboxKey, signatureBytes);
		//add extra Message
		//String message = ((EditText)findViewById(R.id.contactText)).getText().toString();
		//intent.putExtra(Intent.EXTRA_TEXT, message);
		intent.putExtra("Address", ((EditText)findViewById(R.id.address_input)).getText().toString());
		intent.putExtra("Phone", ((EditText)findViewById(R.id.phone_input)).getText().toString());
		intent.putExtra("Name", ((EditText)findViewById(R.id.name_input)).getText().toString());
		intent.putExtra("KeyInfo", currentKeyInfo);
				//start intent

		startActivity(intent);

	}

	/**
	 * when a user submits an address to search
	 * creates view with user's defined address as a point
	 */
	public void navSearchClick(View v) {

		//Build the specific search query
		String query = getString(R.string.geoQuery1) + "286+Ludlow+Avenue" + getString(R.string.geoQuery2) + getString(R.string.testAccessToken );

		//This address is explicit only for debugging purposes. Will depend on user input later.
		String address = ((EditText)findViewById(R.id.navAddressBar)).getText().toString();
		address = address.replace(' ','+');

		//try to get the latitude/longitude from the user-given address
		Context ourContext = MainActivity.this.getApplicationContext();
		LatLng currentLatLng = new LatLng(0,0);
		try {

			currentLatLng = getLocationFromAddress(ourContext, address);
			List<Address> radda = getAddressfromLocation(ourContext, currentLatLng);
			String stop = "stop";

		} catch(Exception e) {}

		if (currentLatLng != null){

		//With our Lat and Lng set, make a navigation_map fragment, and pass the data to it.
		Fragment navMap = new NavigationMap();
		Bundle args = new Bundle();
		args.putDouble("Latitude",  currentLatLng.getLatitude());
		args.putDouble("Longitude", currentLatLng.getLongitude());
		navMap.setArguments(args);

		//Switch context to this bad boy.
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, navMap, "navMapTag")
				.commit();
		}
		else{
			Toast.makeText(this,"Location is not a Valid Location",Toast.LENGTH_LONG).show();
		}
	}

	//Converts an address to a Latitude and Longitude
	public static LatLng getLocationFromAddress(Context context, String strAddress) {

		Geocoder coder = new Geocoder(context);
		List<Address> address;
		LatLng p1 = null;

		//try to convert address
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
			p1 = null;
		}

		return p1;
	}

	//converts latitude/longitude into an address
	public static List<Address> getAddressfromLocation(Context context, LatLng paramLatLng) {

		Geocoder coder = new Geocoder(context);
		List<Address> address = null;
		LatLng p1 = null;

		//attempt to get address from longitude/latitude
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

	public Signature signatureGenerator(String key) {
		KeyPairGenerator kpg = null;
		byte[] data = null;
		//byte[] signatureBytes = null;
		Signature sig = null;
		KeyPair keyPair = null;

		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			keyPair = kpg.genKeyPair();
			data = key.getBytes("UTF8");
			sig = Signature.getInstance("MD5WithRSA");
			sig.initSign(keyPair.getPrivate());
			sig.update(data);
			//signatureBytes = sig.sign();
			//sig.initVerify(keyPair.getPublic());
		//	sig.update(data);

			//sig.

			//sig.initVerify(keyPair.getPublic());
			//sig.update(data);
		}
		catch(Exception e){}



		return sig;
	}

	public class KeyInfo implements Serializable {
		private PublicKey pubkey = null;
		private String data = null;
		private byte[] signatureBytes = null;

		public KeyInfo(PublicKey pubKey, String data, byte[] signatureBytes){
			this.pubkey = pubkey;
			this.data = data;
			this.signatureBytes = signatureBytes;
		}

		public PublicKey getPubKey (){
			return pubkey;
		}

		public byte[] getData (){
			return data.getBytes();
		}

		public byte[] getSignatureBytes (){
			return signatureBytes;
		}
	}
}
