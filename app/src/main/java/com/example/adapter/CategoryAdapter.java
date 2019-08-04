package com.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.placefinderapp.R;
import com.example.item.ItemCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ItemRowHolder> {

    private ArrayList<ItemCategory> dataList, mDataList;
    private Context mContext;
    int count = 0;
    public CategoryAdapter(Context context, ArrayList<ItemCategory> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        mDataList = new ArrayList<>();
        mDataList.addAll(dataList);
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cat_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemCategory singleItem = dataList.get(position);

        Picasso.get().load(singleItem.getCategoryImageBig()).placeholder(R.drawable.place_holder_small).into(holder.image);
        holder.text_cat.setText(singleItem.getCategoryName());

        if (position % 3 == 0) {
            count=count+1;
        }
        if (count % 2 == 0) {
            holder.lay_bg.setBackgroundColor(mContext.getResources().getColor(R.color.cat_bg_1));

        } else {
            holder.lay_bg.setBackgroundColor(mContext.getResources().getColor(R.color.cat_bg_2));

        }

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        private TextView text_cat;
        private RelativeLayout lyt_parent;
        private LinearLayout lay_bg;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            text_cat = itemView.findViewById(R.id.text_cat);
            lay_bg=itemView.findViewById(R.id.lay_bg);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        dataList.clear();
        if (charText.length() == 0) {
            dataList.addAll(mDataList);
        } else {
            for (ItemCategory wp : mDataList) {
                if (wp.getCategoryName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    dataList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
