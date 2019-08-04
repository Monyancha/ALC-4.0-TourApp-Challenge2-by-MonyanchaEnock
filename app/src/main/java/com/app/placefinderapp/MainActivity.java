package com.app.placefinderapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fragment.AllPlacesFragment;
import com.example.fragment.CategoryFragment;
import com.example.fragment.FavouriteFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.LatestFragment;
import com.example.fragment.SettingFragment;
import com.example.item.ItemAbout;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ixidev.gdpr.GDPRChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FragmentManager fragmentManager;
    ArrayList<ItemAbout> mListItem;
    JsonUtils jsonUtils;
    LinearLayout adLayout;
    Menu menu;
    MyApplication MyApp;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    TextView header_tag;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MyApp = MyApplication.getAppInstance();
        JsonUtils.setStatusBarGradiant(MainActivity.this);
        fragmentManager = this.getSupportFragmentManager();
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.d_slidemenu, null);
                toolbar.setNavigationIcon(d);
            }
        });
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.inflateHeaderView(R.layout.nav_header);
        header_tag = hView.findViewById(R.id.header_tag);
        adLayout = findViewById(R.id.adview);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);

        mListItem = new ArrayList<>();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_app_details");
        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            new MyTaskDev(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        mDrawerLayout.closeDrawers();
                        switch (id) {
                            case R.id.nav_home:
                                HomeFragment homeFragment = new HomeFragment();
                                loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                                break;
                            case R.id.nav_cat:
                                CategoryFragment categoryFragment = new CategoryFragment();
                                loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                                break;
                            case R.id.nav_near:
                                Intent intent_near = new Intent(MainActivity.this, NearPlaceActivity.class);
                                startActivity(intent_near);
                                break;
                            case R.id.nav_recent:
                                LatestFragment latestFragment = new LatestFragment();
                                loadFrag(latestFragment, getString(R.string.menu_recent), fragmentManager);
                                break;
                            case R.id.nav_all:
                                AllPlacesFragment allPlacesFragment = new AllPlacesFragment();
                                loadFrag(allPlacesFragment, getString(R.string.menu_all), fragmentManager);
                                break;
                            case R.id.nav_fav:
                                FavouriteFragment favouriteFragment = new FavouriteFragment();
                                loadFrag(favouriteFragment, getString(R.string.menu_favorite), fragmentManager);
                                break;
                            case R.id.nav_setting:
                                SettingFragment settingFragment = new SettingFragment();
                                loadFrag(settingFragment, getString(R.string.menu_setting), fragmentManager);
                                break;
                            case R.id.nav_profile:
                                Intent intent_edt = new Intent(MainActivity.this, ProfileActivity.class);
                                startActivity(intent_edt);
                                break;
                            case R.id.nav_login:
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.nav_logout:
                                Logout();
                                break;
                        }
                        return true;
                    }
                });

        if (MyApp.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }

        if (!MyApp.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
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
                            showToast(getString(R.string.no_data_found));
                        } else {
                            ItemAbout itemAbout = new ItemAbout();

                            itemAbout.setappDevelop(objJson.getString(Constant.APP_DEVELOP));
                            itemAbout.setappBannerId(objJson.getString(Constant.ADS_BANNER_ID));
                            itemAbout.setappFullId(objJson.getString(Constant.ADS_FULL_ID));
                            itemAbout.setappBannerOn(objJson.getString(Constant.ADS_BANNER_ON_OFF));
                            itemAbout.setappFullOn(objJson.getString(Constant.ADS_FULL_ON_OFF));
                            itemAbout.setappFullPub(objJson.getString(Constant.ADS_PUB_ID));
                            itemAbout.setappFullAdsClick(objJson.getString(Constant.ADS_CLICK));

                            mListItem.add(itemAbout);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        if (mListItem.size() != 0) {

            ItemAbout itemAbout = mListItem.get(0);
            Constant.SAVE_ADS_BANNER_ID = itemAbout.getappBannerId();
            Constant.SAVE_ADS_FULL_ID = itemAbout.getappFullId();
            Constant.SAVE_ADS_BANNER_ON_OFF = itemAbout.getappBannerOn();
            Constant.SAVE_ADS_FULL_ON_OFF = itemAbout.getappFullOn();
            Constant.SAVE_ADS_PUB_ID = itemAbout.getappFullPub();
            Constant.SAVE_ADS_CLICK = itemAbout.getappFullAdsClick();
            Constant.SAVE_TAG_LINE = itemAbout.getAppTagLine();
            checkForConsent();

        }
        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);

    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment1, f1, name);
        ft.commitAllowingStateLoss();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle(getString(R.string.app_name));
            alert.setIcon(R.mipmap.app_icon);
            alert.setMessage(getString(R.string.exit_msg));
            alert.setPositiveButton(getString(R.string.exit_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    });
            alert.setNegativeButton(getString(R.string.exit_no), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            alert.show();
        }
    }


    public void checkForConsent() {
        new GDPRChecker()
                .withContext(MainActivity.this)
                .withPrivacyUrl(getString(R.string.gdpr_privacy_link)) // your privacy url add here
                .withPublisherIds(Constant.SAVE_ADS_PUB_ID) // your admob account Publisher id add here
                //.withTestMode("FA0F55855A8169A47EB9D713413B9FE9") // remove this on real live project
                .check();
        JsonUtils.ShowBannerAds(MainActivity.this, adLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void Logout() {

        final PrettyDialog dialog = new PrettyDialog(MainActivity.this);
        dialog.setTitle(getString(R.string.dialog_logout))
                .setTitleColor(R.color.dialog_text)
                .setMessage(getString(R.string.logout_msg))
                .setMessageColor(R.color.dialog_text)
                .setAnimationEnabled(false)
                .setIcon(R.drawable.pdlg_icon_info, R.color.dialog_color, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                    }
                })
                .addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                        MyApp.saveIsLogin(false);
                        Intent intent_login = new Intent(MainActivity.this, SignInActivity.class);
                        intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_login);
                        finish();
                    }
                })
                .addButton(getString(R.string.dialog_no), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApp.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }
                if (!canUseExternalStorage) {
                    Toast.makeText(MainActivity.this, getString(R.string.permission_request), Toast.LENGTH_SHORT).show();
                } else {
                    // user now provided permission
                    // perform function for what you want to achieve
                    Log.i("Permission", "granted");
                }
            }
        }
    }
}