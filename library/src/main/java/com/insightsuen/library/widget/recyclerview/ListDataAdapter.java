package com.insightsuen.library.widget.recyclerview;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by Insight Suen on 2017/12/5.
 * List data RecyclerView
 */

public abstract class ListDataAdapter<Item> extends RecyclerView.Adapter<BindViewHolder<Item>> {

    protected List<Item> mData;

    public ListDataAdapter(List<Item> data) {
        mData = data;
    }

    @Override
    public void onBindViewHolder(BindViewHolder<Item> holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public void onBindViewHolder(BindViewHolder<Item> holder, int position, List<Object> payloads) {
        onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
