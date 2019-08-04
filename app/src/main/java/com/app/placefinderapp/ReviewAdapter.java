package com.app.placefinderapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.item.ItemReview;
import com.github.ornolfr.ratingview.RatingView;

import java.util.ArrayList;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ItemRowHolder> {

    private ArrayList<ItemReview> dataList;
    private Context mContext;

    public ReviewAdapter(Context context, ArrayList<ItemReview> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_review_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemReview singleItem = dataList.get(position);

        if (!singleItem.getReviewName().equals("null")){
            holder.text_user_name.setText(singleItem.getReviewName());
        }

        holder.text_user_review.setText(singleItem.getReviewMessage());
        holder.ratingView.setRating(Float.parseFloat(singleItem.getReviewRate()));

        if (position % 2 == 1) {
            holder.lyt_parent.setBackgroundColor(mContext.getResources().getColor(R.color.review_list_bg_2));

        } else {
            holder.lyt_parent.setBackgroundColor(mContext.getResources().getColor(R.color.review_list_bg_1));

        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView text_user_name, text_user_review;
        private LinearLayout lyt_parent;
        private RatingView ratingView;

        private ItemRowHolder(View itemView) {
            super(itemView);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            text_user_name = itemView.findViewById(R.id.text_user_name);
            text_user_review = itemView.findViewById(R.id.text_user_review);
            ratingView = itemView.findViewById(R.id.ratingView);
        }
    }
}
