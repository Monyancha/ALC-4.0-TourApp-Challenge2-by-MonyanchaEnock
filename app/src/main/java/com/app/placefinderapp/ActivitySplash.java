package com.app.placefinderapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class ActivitySplash extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	MyApplication App;
	String str_package;
	private Location mylocation;
	private GoogleApiClient googleApiClient;
	private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
	private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		JsonUtils.setStatusBarGradiant(ActivitySplash.this);
		App = MyApplication.getAppInstance();
		setUpGClient();
	}
		@SuppressLint("StaticFieldLeak")
		private class MyTaskDev extends AsyncTask<String, Void, String> {

			String base64;

			private MyTaskDev(String base64) {
				this.base64 = base64;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			@Override
			protected String doInBackground(String... params) {
				return JsonUtils.getJSONString(params[0], base64);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				if (null == result || result.length() == 0) {
					showToast(getString(R.string.no_data_found));
				} else {

					try {
						JSONObject mainJson = new JSONObject(result);
						JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ARRAY_NAME);
						JSONObject objJson;
						for (int i = 0; i < jsonArray.length(); i++) {
							objJson = jsonArray.getJSONObject(i);
							if(objJson.has("status")){
								final PrettyDialog dialog = new PrettyDialog(ActivitySplash.this);
								dialog.setTitle(getString(R.string.dialog_error))
										.setTitleColor(R.color.dialog_text)
										.setMessage(getString(R.string.restart_msg))
										.setMessageColor(R.color.dialog_text)
										.setAnimationEnabled(false)
										.setIcon(R.drawable.pdlg_icon_close, R.color.dialog_color, new PrettyDialogCallback() {
											@Override
											public void onClick() {
												dialog.dismiss();
												finish();
											}
										})
										.addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
											@Override
											public void onClick() {
												dialog.dismiss();
												finish();
											}
										});
								dialog.setCancelable(false);
								dialog.show();
							}else {
 							str_package = objJson.getString(Constant.APP_PACKAGE_NAME);

							if (str_package.equals(getPackageName())) {
 								if (App.getIsLogin()) {
									Intent intent = new Intent(getApplicationContext(), MainActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									finish();
								} else {
									Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
									startActivity(intent);
									finish();
								}

							}}

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void showToast (String msg){
			Toast.makeText(ActivitySplash.this, msg, Toast.LENGTH_LONG).show();
		}

	    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

	@Override
	public void onLocationChanged(Location location) {
		mylocation = location;
		if (mylocation != null) {
			Double latitude = mylocation.getLatitude();
			Double longitude = mylocation.getLongitude();
			Constant.USER_LATITUDE = String.valueOf(latitude);
			Constant.USER_LONGITUDE = String.valueOf(longitude);

			JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
			jsObj.addProperty("method_name", "get_app_details");
			if (JsonUtils.isNetworkAvailable(ActivitySplash.this)) {
				new MyTaskDev(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
			} else {
				showToast(getString(R.string.network_msg));
			}
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		checkPermissions();
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CHECK_SETTINGS_GPS:
				switch (resultCode) {
					case Activity.RESULT_OK:
						getMyLocation();
						break;
					case Activity.RESULT_CANCELED:
						permissionReject();
						break;
				}
				break;
		}
	}

	private void checkPermissions() {
		int permissionLocation = ContextCompat.checkSelfPermission(ActivitySplash.this,
				Manifest.permission.ACCESS_FINE_LOCATION);
		List<String> listPermissionsNeeded = new ArrayList<>();
		if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
			if (!listPermissionsNeeded.isEmpty()) {
				ActivityCompat.requestPermissions(this,
						listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
			}
		} else {
			getMyLocation();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		int permissionLocation = ContextCompat.checkSelfPermission(ActivitySplash.this,
				Manifest.permission.ACCESS_FINE_LOCATION);
		if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
			getMyLocation();
		} else {
			permissionReject();
		}
	}

	private void permissionReject() {
		Toast.makeText(ActivitySplash.this, getString(R.string.location_permission), Toast.LENGTH_SHORT).show();
		JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
		jsObj.addProperty("method_name", "get_app_details");
		if (JsonUtils.isNetworkAvailable(ActivitySplash.this)) {
			new MyTaskDev(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
		} else {
			showToast(getString(R.string.network_msg));
		}
	}

	private void getMyLocation() {
		if (googleApiClient != null) {
			if (googleApiClient.isConnected()) {
				int permissionLocation = ContextCompat.checkSelfPermission(ActivitySplash.this,
						Manifest.permission.ACCESS_FINE_LOCATION);
				if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
					mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
					LocationRequest locationRequest = new LocationRequest();
					locationRequest.setInterval(1000 * 1000);
					locationRequest.setFastestInterval(1000 * 1000);
					locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
					LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
							.addLocationRequest(locationRequest);
					builder.setAlwaysShow(true);
					LocationServices.FusedLocationApi
							.requestLocationUpdates(googleApiClient, locationRequest, this);
					PendingResult<LocationSettingsResult> result =
							LocationServices.SettingsApi
									.checkLocationSettings(googleApiClient, builder.build());
					result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

						@Override
						public void onResult(LocationSettingsResult result) {
							final Status status = result.getStatus();
							switch (status.getStatusCode()) {
								case LocationSettingsStatusCodes.SUCCESS:
									// All location settings are satisfied.
									// You can initialize location requests here.
									int permissionLocation = ContextCompat
											.checkSelfPermission(ActivitySplash.this,
													Manifest.permission.ACCESS_FINE_LOCATION);
									if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
										mylocation = LocationServices.FusedLocationApi
												.getLastLocation(googleApiClient);
									}
									break;
								case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
									// Location settings are not satisfied.
									// But could be fixed by showing the user a dialog.
									try {
										// Show the dialog by calling startResolutionForResult(),
										// and check the result in onActivityResult().
										// Ask to turn on GPS automatically
										status.startResolutionForResult(ActivitySplash.this,
												REQUEST_CHECK_SETTINGS_GPS);
									} catch (IntentSender.SendIntentException e) {
										// Ignore the error.
									}
									break;
								case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
									// Location settings are not satisfied.
									// However, we have no way
									// to fix the
									// settings so we won't show the dialog.
									// finish();
									break;
							}
						}
					});
				}
			}
		}
	}
	}
