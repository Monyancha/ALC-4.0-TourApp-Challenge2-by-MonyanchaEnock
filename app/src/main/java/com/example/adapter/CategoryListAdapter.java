package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.placefinderapp.ActivityDetail;
import com.app.placefinderapp.R;
import com.example.item.ItemPlaceList;
import com.example.util.OnLoadMoreListener;
import com.example.util.PopUpAds;
import com.github.ornolfr.ratingview.RatingView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ItemRowHolder> {

    private ArrayList<ItemPlaceList> dataList;
    private Context mContext;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public CategoryListAdapter(Context context, ArrayList<ItemPlaceList> dataList, RecyclerView recyclerView) {
        this.dataList = dataList;
        this.mContext = context;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_list_item, parent, false);
            return new ContentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    public class ProgressViewHolder extends ItemRowHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_ITEM) {
            final ContentViewHolder holder = (ContentViewHolder) viewHolder;
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
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ItemRowHolder(View itemView) {
            super(itemView);
        }
    }

    public class ContentViewHolder extends ItemRowHolder {
        public ImageView image;
        private TextView text_title, text_address, text_rate_total, text_avg_rate;
        private RelativeLayout lyt_parent;
        private RatingView ratingView;

        private ContentViewHolder(View itemView) {
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
