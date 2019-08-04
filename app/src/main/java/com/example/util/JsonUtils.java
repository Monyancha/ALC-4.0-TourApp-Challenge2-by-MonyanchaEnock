package com.example.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.app.placefinderapp.R;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ixidev.gdpr.GDPRChecker;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JsonUtils {

    private Context _context;
    public static boolean personalization_ad = false;

    // constructor
    public JsonUtils(Context context) {
        this._context = context;
    }

    public static String getJSONString(String url) {
        String jsonString = null;
        HttpURLConnection linkConnection = null;
        try {
            URL linkurl = new URL(url);
            linkConnection = (HttpURLConnection) linkurl.openConnection();
            int responseCode = linkConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream linkinStream = linkConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int j = 0;
                while ((j = linkinStream.read()) != -1) {
                    baos.write(j);
                }
                byte[] data = baos.toByteArray();
                jsonString = new String(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (linkConnection != null) {
                linkConnection.disconnect();
            }
        }
        return jsonString;
    }


    public static String getJSONString(String url, String base64) {
        String jsonString = null;
        HttpURLConnection con = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postJsonData = "data=" + base64;
            byte[] postDataBytes = postJsonData.getBytes("UTF-8");
            con.setDoOutput(true);
            con.getOutputStream().write(postDataBytes);
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream linkinStream = con.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int j = 0;
                while ((j = linkinStream.read()) != -1) {
                    baos.write(j);
                }
                byte[] data = baos.toByteArray();
                jsonString = new String(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return jsonString;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }
    final static String reg = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";

    public static String getVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().length() <= 0)
            return null;

        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);

        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    public void forceRTLIfSupported(Window window) {
        if (_context.getResources().getString(R.string.isRTL).equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    public static void ShowBannerAds(Context context, LinearLayout mAdViewLayout) {
        if (Constant.SAVE_ADS_BANNER_ON_OFF.equals("true")) {
            AdView mAdView = new AdView(context);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(Constant.SAVE_ADS_BANNER_ID);
            AdRequest.Builder builder = new AdRequest.Builder();
            GDPRChecker.Request request = GDPRChecker.getRequest();
            if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                // load non Personalized ads
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
            } // else do nothing , it will load PERSONALIZED ads
            mAdView.loadAd(builder.build());
            mAdViewLayout.addView(mAdView);
        } else {
            mAdViewLayout.setVisibility(View.GONE);
        }
    }
    public static String Format(Integer number){
        String[] suffix = new String[]{"k","m","b","t"};
        int size = (number.intValue() != 0) ? (int) Math.log10(number) : 0;
        if (size >= 3){
            while (size % 3 != 0) {
                size = size - 1;
            }
        }
        double notation = Math.pow(10, size);
        String result = (size >= 3) ? + (Math.round((number / notation) * 100) / 100.0d)+suffix[(size/3) - 1] : + number + "";
        return result;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
