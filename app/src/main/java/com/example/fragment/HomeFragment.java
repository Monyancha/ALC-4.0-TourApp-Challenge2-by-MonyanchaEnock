package com.example.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.app.placefinderapp.MainActivity;
import com.app.placefinderapp.MyApplication;
import com.app.placefinderapp.R;
import com.app.placefinderapp.SearchActivity;
import com.example.adapter.CategoryAdapter;
import com.example.item.ItemCategory;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.example.util.RecyclerTouchListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    NestedScrollView mScrollView;
    ProgressBar mProgressBar;
    ArrayList<ItemCategory> mCatList;
    RecyclerView mCatView;
    CategoryAdapter categoryAdapter;
    EditText edt_search;
    MyApplication MyApp;
    private LinearLayout lyt_not_found;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        MyApp = MyApplication.getAppInstance();
        mCatList = new ArrayList<>();

        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        mCatView = rootView.findViewById(R.id.rv_latest_cat);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        edt_search = rootView.findViewById(R.id.edt_search);

        mCatView.setHasFixedSize(true);
        mCatView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mCatView.setFocusable(false);
        mCatView.setNestedScrollingEnabled(false);


        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_home");
        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new Home(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }

         mCatView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mCatView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {

                    Constant.AD_COUNT++;
                    if (Constant.AD_COUNT == Integer.parseInt(Constant.SAVE_ADS_CLICK)) {
                        Constant.AD_COUNT = 0;
                        LoadingDialog();
                        final InterstitialAd mInterstitial = new InterstitialAd(requireActivity());
                        mInterstitial.setAdUnitId(Constant.SAVE_ADS_FULL_ID);
                        AdRequest adRequest;
                        if (JsonUtils.personalization_ad) {
                            adRequest = new AdRequest.Builder()
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }
                        mInterstitial.loadAd(adRequest);
                        mInterstitial.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // TODO Auto-generated method stub
                                super.onAdLoaded();
                                pDialog.dismiss();
                                if (mInterstitial.isLoaded()) {
                                    mInterstitial.show();
                                }
                            }

                            public void onAdClosed() {
                                String categoryName = mCatList.get(position).getCategoryName();
                                Bundle bundle = new Bundle();
                                bundle.putString("name", categoryName);
                                bundle.putString("Id", mCatList.get(position).getCategoryId());

                                FragmentManager fm = getFragmentManager();
                                CategoryListFragment subCategoryFragment = new CategoryListFragment();
                                subCategoryFragment.setArguments(bundle);
                                assert fm != null;
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.hide(HomeFragment.this);
                                ft.add(R.id.fragment1, subCategoryFragment, categoryName);
                                ft.addToBackStack(categoryName);
                                ft.commitAllowingStateLoss();
                                ((MainActivity) requireActivity()).setToolbarTitle(categoryName);
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                pDialog.dismiss();
                                String categoryName = mCatList.get(position).getCategoryName();
                                Bundle bundle = new Bundle();
                                bundle.putString("name", categoryName);
                                bundle.putString("Id", mCatList.get(position).getCategoryId());

                                FragmentManager fm = getFragmentManager();
                                CategoryListFragment subCategoryFragment = new CategoryListFragment();
                                subCategoryFragment.setArguments(bundle);
                                assert fm != null;
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.hide(HomeFragment.this);
                                ft.add(R.id.fragment1, subCategoryFragment, categoryName);
                                ft.addToBackStack(categoryName);
                                ft.commitAllowingStateLoss();
                                ((MainActivity) requireActivity()).setToolbarTitle(categoryName);
                            }
                        });
                    } else {
                        String categoryName = mCatList.get(position).getCategoryName();
                        Bundle bundle = new Bundle();
                        bundle.putString("name", categoryName);
                        bundle.putString("Id", mCatList.get(position).getCategoryId());

                        FragmentManager fm = getFragmentManager();
                        CategoryListFragment subCategoryFragment = new CategoryListFragment();
                        subCategoryFragment.setArguments(bundle);
                        assert fm != null;
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.hide(HomeFragment.this);
                        ft.add(R.id.fragment1, subCategoryFragment, categoryName);
                        ft.addToBackStack(categoryName);
                        ft.commitAllowingStateLoss();
                        ((MainActivity) requireActivity()).setToolbarTitle(categoryName);
                    }
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //do something
                    String st_search = edt_search.getText().toString();
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("search", st_search);
                    startActivity(intent);
                    edt_search.getText().clear();
                }
                return false;
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class Home extends AsyncTask<String, Void, String> {

        String base64;

        private Home(String base64) {
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
                    JSONObject jsonArray = mainJson.getJSONObject(Constant.CATEGORY_ARRAY_NAME);
                    JSONArray json = jsonArray.getJSONArray("cat_list");
                    JSONObject objJson;
                    for (int i = 0; i < json.length(); i++) {
                        objJson = json.getJSONObject(i);
                        if (objJson.has("status")) {
                            lyt_not_found.setVisibility(View.VISIBLE);
                        } else {
                            ItemCategory objItem = new ItemCategory();
                            objItem.setCategoryId(objJson.getString(Constant.CATEGORY_CID));
                            objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                            objItem.setCategoryImageBig(objJson.getString(Constant.CATEGORY_IMAGE));

                            mCatList.add(objItem);
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
        if (getActivity() != null) {
            categoryAdapter = new CategoryAdapter(getActivity(), mCatList);
            mCatView.setAdapter(categoryAdapter);

            if (categoryAdapter.getItemCount() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                lyt_not_found.setVisibility(View.GONE);
            }
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            mCatView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mCatView.setVisibility(View.VISIBLE);
        }
    }

    private void LoadingDialog() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

    }
}
