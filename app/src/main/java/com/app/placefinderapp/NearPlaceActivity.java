package com.app.placefinderapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.item.ItemCategory;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.example.util.NothingSelectedSpinnerAdapter;
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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NearPlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    LinearLayout adLayout;
    Spinner spinnerCategory, spinnerDistance;
    CardView card_view;
    ProgressBar mProgressBar;
    private LinearLayout lyt_not_found;
    ArrayList<ItemCategory> itemCategories;
    ArrayList<String> mCategoryName;
    String srt_distance[];
    Button button_search;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_place);
        Toolbar toolbar = findViewById(R.id.toolbar);
        JsonUtils.setStatusBarGradiant(NearPlaceActivity.this);
        toolbar.setTitle(R.string.menu_near);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        itemCategories = new ArrayList<>();
        mCategoryName = new ArrayList<>();
        adLayout = findViewById(R.id.adLayout);
        JsonUtils.ShowBannerAds(NearPlaceActivity.this, adLayout);
        srt_distance = getResources().getStringArray(R.array.dis_array);

        spinnerCategory = findViewById(R.id.spCategory);
        card_view = findViewById(R.id.card_view);
        mProgressBar = findViewById(R.id.progressBar);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        spinnerDistance = findViewById(R.id.spDistance);
        button_search = findViewById(R.id.button_login_activity);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_category");
        if (JsonUtils.isNetworkAvailable(NearPlaceActivity.this)) {
            new getCategoryData(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class getCategoryData extends AsyncTask<String, Void, String> {

        String base64;

        private getCategoryData(String base64) {
            this.base64 = base64;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if (null == result || result.length() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has("status")) {
                            lyt_not_found.setVisibility(View.VISIBLE);
                        } else {

                            ItemCategory objItem = new ItemCategory();
                            objItem.setCategoryId(objJson.getString(Constant.CATEGORY_CID));
                            objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                            objItem.setCategoryImageBig(objJson.getString(Constant.CATEGORY_IMAGE));
                            mCategoryName.add(objJson.getString(Constant.CATEGORY_NAME));
                            itemCategories.add(objItem);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult2();
            }
        }
    }

    private void setResult2() {

        ArrayAdapter<String> typeAdapter2 = new ArrayAdapter<>(NearPlaceActivity.this, R.layout.spinner_item_home, mCategoryName);
        typeAdapter2.setDropDownViewResource(R.layout.spinner_item_home);
        spinnerCategory.setAdapter(
                new NothingSelectedSpinnerAdapter(typeAdapter2,
                        R.layout.contact_spinner_row_nothing_selected_home, NearPlaceActivity.this));
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.green));
                    ((TextView) parent.getChildAt(0)).setTextSize(13);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.green));
                    ((TextView) parent.getChildAt(0)).setTextSize(13);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(NearPlaceActivity.this, android.R.layout.simple_list_item_1, srt_distance);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item_home);
        spinnerDistance.setAdapter(typeAdapter);


        spinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.green));
                    ((TextView) parent.getChildAt(0)).setTextSize(13);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.green));
                    ((TextView) parent.getChildAt(0)).setTextSize(13);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.USER_LATITUDE == null && Constant.USER_LONGITUDE == null) {
                    setUpGClient();
                }else {
                    Intent intent = new Intent(NearPlaceActivity.this, NearSearchActivity.class);
                    intent.putExtra("DisId", String.valueOf(spinnerDistance.getSelectedItem()));
                    intent.putExtra("CatId", itemCategories.get(spinnerCategory.getSelectedItemPosition()- 1).getCategoryId());
                    startActivity(intent);
                }


            }
        });


    }

    private void showProgress(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            card_view.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            card_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
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
        int permissionLocation = ContextCompat.checkSelfPermission(NearPlaceActivity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(NearPlaceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {
            permissionReject();
        }
    }

    private void permissionReject() {
        new AlertDialog.Builder(NearPlaceActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.location_permission))
                .setIcon(R.mipmap.app_icon)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(NearPlaceActivity.this,
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
                                            .checkSelfPermission(NearPlaceActivity.this,
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
                                        status.startResolutionForResult(NearPlaceActivity.this,
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
