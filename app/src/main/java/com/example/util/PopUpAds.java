package com.example.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.app.placefinderapp.ActivityDetail;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class PopUpAds {

    public static void ShowInterstitialAds(final Context context, final String Id) {

        if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {

            Constant.AD_COUNT++;
            if (Constant.AD_COUNT == Integer.parseInt(Constant.SAVE_ADS_CLICK)) {
                Constant.AD_COUNT = 0;
                final InterstitialAd mInterstitial = new InterstitialAd(context);
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
                        if (mInterstitial.isLoaded()) {
                            mInterstitial.show();
                        }
                    }

                    public void onAdClosed() {
                        Intent intent_detail = new Intent(context, ActivityDetail.class);
                        intent_detail.putExtra("Id", Id);
                        context.startActivity(intent_detail);

                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Intent intent_detail = new Intent(context, ActivityDetail.class);
                        intent_detail.putExtra("Id", Id);
                        context.startActivity(intent_detail);
                    }
                });
            } else {
                Intent intent_detail = new Intent(context, ActivityDetail.class);
                intent_detail.putExtra("Id", Id);
                context.startActivity(intent_detail);
            }
        }
    }
}
