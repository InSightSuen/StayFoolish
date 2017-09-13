package com.insightsuen.stayfoolish.ui.orm;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.insightsuen.bindroid.component.recyclerview.BindAdapter;
import com.insightsuen.bindroid.utils.ViewModelUtil;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.stayfoolish.OrmBinding;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;
import com.insightsuen.stayfoolish.databinding.NoteItemBinding;

/**
 * Created by InSight Suen on 2017/9/12.
 */

public class OrmActivity extends BaseActivity<OrmBinding> {

    private static final String EXTRA_VIEW_MODEL = "OrmViewModel";

    public static void start(Context context) {
        Intent starter = new Intent(context, OrmActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWidgets();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_orm;
    }

    @Nullable
    @Override
    protected LifecycleViewModel createOrFindViewModel(@Nullable Bundle savedInstanceState) {
        LifecycleViewModel viewModel = ViewModelUtil.findFromFragmentManger(
                getSupportFragmentManager(), EXTRA_VIEW_MODEL);
        if (viewModel == null) {
            viewModel = new OrmViewModel();
            ViewModelUtil.addToFragmentManager(getSupportFragmentManager(), viewModel, EXTRA_VIEW_MODEL);
        }
        return viewModel;
    }

    private void initWidgets() {
        BindAdapter adapter = new NoteAdapter();
        mBinding.rvNotes.setAdapter(adapter);
        mBinding.rvNotes.setLayoutManager(new LinearLayoutManager(this));
    }

    private static class NoteAdapter extends BindAdapter<NoteItemViewModel> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).onBind(mItems.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mItems != null ? mItems.size() : 0;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private NoteItemBinding mBinding;

            private ViewHolder(View itemView) {
                super(itemView);
                mBinding = DataBindingUtil.bind(itemView);
            }

            private void onBind(NoteItemViewModel viewModel) {
                mBinding.setViewModel(viewModel);
                mBinding.executePendingBindings();
            }
        }
    }
}
