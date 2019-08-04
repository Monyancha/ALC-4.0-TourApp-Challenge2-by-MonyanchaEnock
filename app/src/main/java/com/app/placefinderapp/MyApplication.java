package com.app.placefinderapp;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;

import com.example.util.Constant;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application {

    public static MyApplication instance;
    public SharedPreferences LastPositionHolder;
    public SharedPreferences preferences;
    public String prefName = "app";

    public static MyApplication getAppInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("myfonts/Montserrat-SemiBold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    public MyApplication() {
        instance = this;
    }

    public void saveIsNotification(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsNotification", flag);
        editor.apply();
    }
    public boolean getNotification() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsNotification", true);
    }

    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.apply();
    }

    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedIn", false);
    }

    public void saveLogin(String user_id, String user_name, String email) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", user_id);
        editor.putString("user_name", user_name);
        editor.putString("email", email);
        editor.apply();
    }

    public String getUserId() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_id", "");
    }

    public String getUserName() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_name", "");
    }

    public String getUserEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("email", "");
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            JSONObject data = result.notification.payload.additionalData;
            String customKey;
            String isExternalLink;
            if (data != null) {
                customKey = data.optString("place_id", null);
                Constant.LATEST_PLACE_IDD = customKey;
                isExternalLink = data.optString("external_link", null);
                if (customKey != null) {
                    if (!customKey.equals("0")) {
                        Intent intent = new Intent(MyApplication.this, ActivityDetail.class);
                        intent.putExtra("Id", Constant.LATEST_PLACE_IDD);
                        intent.putExtra("isNotification", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        if (!isExternalLink.equals("false")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(isExternalLink));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MyApplication.this, ActivitySplash.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }
            }
        }

    }
}
