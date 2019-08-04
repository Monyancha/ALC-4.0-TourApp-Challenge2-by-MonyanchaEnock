package com.app.placefinderapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ProfileActivity extends AppCompatActivity {

    TextView edtFullName;
    TextView edtEmail;
    TextView edtPassword;
    TextView edtMobile;
    MyApplication MyApp;
    JsonUtils jsonUtils;
    Toolbar toolbar;
    ProgressDialog pDialog;
    Menu menu;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        JsonUtils.setStatusBarGradiant(ProfileActivity.this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_profile));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        MyApp = MyApplication.getAppInstance();
        pDialog = new ProgressDialog(ProfileActivity.this);

        edtFullName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtMobile = findViewById(R.id.edt_phone);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_profile");
        jsObj.addProperty("user_id", MyApp.getUserId());
        if (JsonUtils.isNetworkAvailable(ProfileActivity.this)) {
            new MyTask(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        } else {
            showToast(getString(R.string.network_msg));
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<String, Void, String> {

        String base64;

        private MyTask(String base64) {
            this.base64 = base64;
        }

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ProfileActivity.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data_found));
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has("status")) {
                            showToast(getString(R.string.no_data_found));
                        } else {
                            edtFullName.setText(objJson.getString(Constant.USER_NAME));
                            edtEmail.setText(objJson.getString(Constant.USER_EMAIL));
                            edtMobile.setText(objJson.getString(Constant.USER_PHONE));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_edit:
                Intent intent_edit = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                startActivity(intent_edit);
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

}
