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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ProfileEditActivity extends AppCompatActivity implements Validator.ValidationListener {

    EditText edtFullName;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtMobile;
    private Validator validator;
    MyApplication MyApp;
    String strFullname, strEmail, strPassword, strMobi, strMessage;
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
        setContentView(R.layout.activity_profile_edit);
        JsonUtils.setStatusBarGradiant(ProfileEditActivity.this);
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
        pDialog = new ProgressDialog(ProfileEditActivity.this);

        edtFullName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtMobile = findViewById(R.id.edt_phone);

        validator = new Validator(this);
        validator.setValidationListener(this);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_profile");
        jsObj.addProperty("user_id", MyApp.getUserId());
        if (JsonUtils.isNetworkAvailable(ProfileEditActivity.this)) {
            new MyTask(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        } else {
            showToast(getString(R.string.network_msg));
        }

        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    @Override
    public void onValidationSucceeded() {
        strPassword = edtPassword.getText().toString();

            if (JsonUtils.isNetworkAvailable(ProfileEditActivity.this)) {
                strFullname = edtFullName.getText().toString().replace(" ", "%20");
                strEmail = edtEmail.getText().toString();
                strPassword = edtPassword.getText().toString();
                strMobi = edtMobile.getText().toString();

                JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                jsObj.addProperty("method_name", "user_profile_update");
                jsObj.addProperty("user_id", MyApp.getUserId());
                jsObj.addProperty("name", strFullname);
                jsObj.addProperty("email", strEmail);
                jsObj.addProperty("password", strPassword);
                jsObj.addProperty("phone", strMobi);
                if (JsonUtils.isNetworkAvailable(ProfileEditActivity.this)) {
                    new MyTaskUpdate(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
             } else {
                showToast(getString(R.string.network_msg));
            }
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(this, "Record Not Saved", Toast.LENGTH_SHORT).show();
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

            pDialog = new ProgressDialog(ProfileEditActivity.this);
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

    @SuppressLint("StaticFieldLeak")
    private class MyTaskUpdate extends AsyncTask<String, Void, String> {

        String base64;

        private MyTaskUpdate(String base64) {
            this.base64 = base64;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data_found));

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle(getString(R.string.dialog_error))
                    .setTitleColor(R.color.dialog_color)
                    .setMessage(strMessage)
                    .setMessageColor(R.color.dialog_color)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.dialog_ok), R.color.pdlg_color_white, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            MyApp.saveLogin(MyApp.getUserId(), strFullname, strEmail);
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle(getString(R.string.dialog_success))
                    .setTitleColor(R.color.dialog_color)
                    .setMessage(strMessage)
                    .setMessageColor(R.color.dialog_color)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_success, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.dialog_ok), R.color.pdlg_color_white, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ProfileEditActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                validator.validate();
                break;


            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

}
