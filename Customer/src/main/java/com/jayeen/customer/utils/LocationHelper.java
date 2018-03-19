package com.jayeen.customer.utils;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.jayeen.customer.MainDrawerActivity;

public class LocationHelper implements LocationListener,
		OnConnectionFailedListener, ConnectionCallbacks {

	public interface OnLocationReceived {
		public void onLocationReceived(LatLng latlong);

		public void onLocationReceived(Location location);

		public void onConntected(Bundle bundle);

		public void onConntected(Location location);
	}

	private LocationRequest mLocationRequest;
	private GoogleApiClient mGoogleApiClient;
	private static final int REQUEST_CODE = 2;
	private static final int REQUEST_CODE_LOCATION = 2;
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;
	private OnLocationReceived mLocationReceived;

	private Context context;
	public final String APPTAG = "LocationSample";

	private LatLng latLong;

	private final int INTERVAL = 10000;
	private final int FAST_INTERVAL = 8000;
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private boolean isLocationReceived = false;

	public LocationHelper(Context context) {
		this.context = context;
		createLocationRequest();
		buildGoogleApiClient();
		if (!mResolvingError) { // more about this later
			mGoogleApiClient.connect();
		}

	}

	public void setLocationReceivedLister(OnLocationReceived mLocationReceived) {
		this.mLocationReceived = mLocationReceived;
	}

	public LatLng getCurrentLocation() {
		return latLong;
	}

	public LatLng getLastLatLng() {
		// If Google Play Services is available
		if (servicesConnected()) {
			Location location = null;
			if (mGoogleApiClient.isConnected()) {
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
						|| ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					location = LocationServices.FusedLocationApi
							.getLastLocation(mGoogleApiClient);
				}

			}
			// Display the current location in the UI
			latLong = getLatLng(location);
		}
		return latLong;
	}

	public Location getLastLocation() {
		// If Google Play Services is available
		if (servicesConnected()) {
			Location location = null;
			if (mGoogleApiClient.isConnected()) {
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
						|| ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

					Toast.makeText(context, "Inside Helper loca", Toast.LENGTH_SHORT).show();
					location = LocationServices.FusedLocationApi
							.getLastLocation(mGoogleApiClient);
				}
			}
			latLong = getLatLng(location);
		}
		Location location = new Location("");
		if (latLong != null) {
			location.setLatitude(latLong.latitude);
			location.setLongitude(latLong.longitude);
			return location;
		}
		return null;
	}

	public void onStart() {
		if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
			mGoogleApiClient.connect();
		} else {
			startPeriodicUpdates();
		}
	}

	public void onResume() {
		if (mGoogleApiClient.isConnected()) {
			startPeriodicUpdates();
		}
	}

	public void onPause() {
		if (mGoogleApiClient.isConnected()) {
			stopPeriodicUpdates();
		}
	}

	public void onStop() {
		// If the client is connected
		if (mGoogleApiClient.isConnected()) {
			stopPeriodicUpdates();
		}
		// After disconnect() is called, the client is considered "dead".
		mGoogleApiClient.disconnect();
	}

	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// Continue
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!isLocationReceived) {
			mLocationReceived.onConntected(location);
			isLocationReceived = true;
		}
		if (mLocationReceived != null) {
			mLocationReceived.onLocationReceived(location);
		}
		latLong = getLatLng(location);
		if (mLocationReceived != null && latLong != null) {
			mLocationReceived.onLocationReceived(latLong);
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// TODO Auto-generated method stub
		if (mResolvingError) {
			return;
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		startPeriodicUpdates();
		if (mLocationReceived != null)
			mLocationReceived.onConntected(connectionHint);
	}

	private void startPeriodicUpdates() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
					|| ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				Location location = LocationServices.FusedLocationApi
						.getLastLocation(mGoogleApiClient);
				if (location != null) {
					onLocationChanged(location);
				}
				LocationServices.FusedLocationApi.requestLocationUpdates(
						mGoogleApiClient, mLocationRequest, this);
			} else {
				ActivityCompat.requestPermissions((MainDrawerActivity)context,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						REQUEST_CODE_LOCATION);
			}
		}   //app version check
		else{
			Location location = LocationServices.FusedLocationApi
					.getLastLocation(mGoogleApiClient);
			if (location != null) {
				onLocationChanged(location);
			}
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);
		}

	}

	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions,
										   int[] grantResults) {
		if (requestCode == REQUEST_CODE_LOCATION) {
			if (grantResults.length == 1
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					Location location = LocationServices.FusedLocationApi
							.getLastLocation(mGoogleApiClient);
					if (location != null) {
						onLocationChanged(location);
					}
					LocationServices.FusedLocationApi.requestLocationUpdates(
							mGoogleApiClient, mLocationRequest, this);
					return;
				}


			} else {
			}
		}
	}
	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);

	}


	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 *            An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(INTERVAL);
		mLocationRequest.setFastestInterval(FAST_INTERVAL);
		mLocationRequest.setSmallestDisplacement(50); //added
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	protected synchronized void buildGoogleApiClient() {

		mGoogleApiClient = new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();

	}

	@Override
	public void onConnectionSuspended(int arg0) {

	}

	public LatLng getLatLng(Location currentLocation) {
		// If the location is valid
		if (currentLocation != null) {
			// Return the latitude and longitude as strings
			LatLng latLong = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());

			return latLong;
		} else {
			// Otherwise, return the empty string
			return null;
		}
	}
}
