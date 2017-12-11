package com.insightsuen.library.widget.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Insight Suen on 2017/12/5.
 * Simple RecyclerView adapter
 */

public class SimpleAdapter extends ListDataAdapter<String>{

    public SimpleAdapter(List<String> data) {
        super(data);
    }

    @Override
    public BindViewHolder<String> onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SimpleViewHolder(itemView);
    }

    private static class SimpleViewHolder extends BindViewHolder<String> {

        TextView tvText;

        private SimpleViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        protected void bind(String o) {
            if (tvText != null) {
                tvText.setText(o);
            }
        }
    }
}
