package com.app.placefinderapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ForgotPasswordActivity extends AppCompatActivity implements Validator.ValidationListener {

    @Required(order = 1)
    @Email(order = 2, message = "Please Check and Enter a valid Email Address")
    EditText edtEmail;

    String strEmail, strMessage;
    private Validator validator;
    Button btnSubmit;
    JsonUtils jsonUtils;
    Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        JsonUtils.setStatusBarGradiant(ForgotPasswordActivity.this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.forgot_password));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        edtEmail = findViewById(R.id.editText_fp);
        btnSubmit = findViewById(R.id.button_fp);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                validator.validateAsync();
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    @Override
    public void onValidationSucceeded() {
        strEmail = edtEmail.getText().toString();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "forgot_pass");
        jsObj.addProperty("user_email", strEmail);
        if (JsonUtils.isNetworkAvailable(ForgotPasswordActivity.this)) {
            new MyTaskForgot(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
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
    private class MyTaskForgot extends AsyncTask<String, Void, String> {

        String base64;

        private MyTaskForgot(String base64) {
            this.base64 = base64;
        }

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
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
                        if(objJson.has("status")){
                            showToast(getString(R.string.no_data_found));
                        }else {
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            edtEmail.setText("");
            edtEmail.requestFocus();
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle(getString(R.string.dialog_error))
                    .setTitleColor(R.color.dialog_text)
                    .setMessage(strMessage)
                    .setMessageColor(R.color.dialog_text)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle(getString(R.string.dialog_success))
                    .setTitleColor(R.color.dialog_text)
                    .setMessage(strMessage)
                    .setMessageColor(R.color.dialog_text)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_success, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            Intent intent_co = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                            intent_co.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_co);
                            finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();

        }
    }

    public void showToast(String msg) {
        Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
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
}
