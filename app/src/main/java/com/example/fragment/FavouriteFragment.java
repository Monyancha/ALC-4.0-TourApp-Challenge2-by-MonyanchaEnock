package com.example.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.placefinderapp.R;
import com.example.adapter.FavAdapter;
import com.example.favorite.DatabaseHelper;
import com.example.item.ItemPlaceList;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {

    ArrayList<ItemPlaceList> mListItem;
    public RecyclerView recyclerView;
    FavAdapter adapter;
    private LinearLayout lyt_not_found;
    DatabaseHelper databaseHelper;
    TextView no_fav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        mListItem = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getActivity());
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);
        no_fav = rootView.findViewById(R.id.no_fav);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setFocusable(false);

        return rootView;
    }

    private void displayData() {

        adapter = new FavAdapter(getActivity(), mListItem);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
            no_fav.setText(getString(R.string.no_favorite));
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListItem = databaseHelper.getFavourite();
        displayData();
    }
}
