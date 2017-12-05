package com.insightsuen.library.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Insight Suen on 2017/12/5.
 * Bind-able ViewHolder
 */

public abstract class BindViewHolder<Data> extends RecyclerView.ViewHolder {

    public BindViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract void bind(Data data);
}
