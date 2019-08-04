package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.placefinderapp.ActivityDetail;
import com.app.placefinderapp.R;
import com.example.item.ItemPlaceList;
import com.example.util.PopUpAds;
import com.github.ornolfr.ratingview.RatingView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemRowHolder> {

    private ArrayList<ItemPlaceList> dataList;
    private Context mContext;

    public SearchAdapter(Context context, ArrayList<ItemPlaceList> dataList) {
        this.dataList = dataList;
        this.mContext = context;
     }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_list_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemPlaceList singleItem = dataList.get(position);

        Picasso.get().load(singleItem.getPlaceImage()).placeholder(R.drawable.place_holder_small).into(holder.image);
        holder.text_title.setText(singleItem.getPlaceName());
        holder.text_address.setText(singleItem.getPlaceAddress());
        holder.text_rate_total.setText(singleItem.getPlaceRateTotal() + " " + mContext.getString(R.string.rate_place_title));

        if (singleItem.getPlaceRateAvg().isEmpty()) {
            holder.text_avg_rate.setText("0");
        } else {
            holder.text_avg_rate.setText(singleItem.getPlaceRateAvg());
        }
        if (singleItem.getPlaceRateAvg().isEmpty()) {
            holder.ratingView.setRating(0);
        } else {
            holder.ratingView.setRating(Float.parseFloat(singleItem.getPlaceRateAvg()));
        }


        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpAds.ShowInterstitialAds(mContext,singleItem.getPlaceId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        private TextView text_title, text_address, text_rate_total, text_avg_rate;
        private RelativeLayout lyt_parent;
        private RatingView ratingView;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            text_title = itemView.findViewById(R.id.text_place_title);
            text_address = itemView.findViewById(R.id.text_place_address);
            text_rate_total = itemView.findViewById(R.id.text_place_rate_total);
            text_avg_rate = itemView.findViewById(R.id.text_place_rate_Avg);
            ratingView = itemView.findViewById(R.id.ratingView);
        }
    }

}
