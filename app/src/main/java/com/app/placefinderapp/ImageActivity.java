package com.app.placefinderapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.example.util.TouchImageView;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ImageActivity extends AppCompatActivity {

    Toolbar toolbar;
    JsonUtils jsonUtils;
    TouchImageView imageView;
    ViewPager mViewPager;
    CustomViewPagerAdapter mAdapter;
    TextView textViewClose;
    int position;
    int TOTAL_IMAGE;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        textViewClose=findViewById(R.id.txt_close);

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        Intent i = getIntent();
        position = i.getIntExtra("POSITION_ID", 0);

        TOTAL_IMAGE = Constant.ConsImage.size() - 1;

        mViewPager = findViewById(R.id.viewPager);
        mAdapter = new CustomViewPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(position);
        textViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private class CustomViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        public CustomViewPagerAdapter() {
            // TODO Auto-generated constructor stub
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.ConsImage.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View imageLayout = inflater.inflate(R.layout.row_full_gallery_item, container, false);
            assert imageLayout != null;
            TouchImageView image = imageLayout.findViewById(R.id.iv_wall_details);
            TextView text = imageLayout.findViewById(R.id.textNumber);

            text.setText(position + 1 + "/" + Constant.ConsImage.size());

              Picasso.get().load(Constant.ConsImage.get(position).getCategoryImageBig()).placeholder(R.mipmap.app_icon).into(image);

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
