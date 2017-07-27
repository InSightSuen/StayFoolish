package com.insightsuen.stayfoolish.ui.drag;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;
import com.insightsuen.stayfoolish.model.SimpleData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by InSight Suen on 2017/7/13.
 * Drag and drop test Activity
 */
public class DragActivity extends BaseActivity implements
        OnStartDragListener,
        ItemTouchCallbacks {

    public static void start(Context context) {
        Intent starter = new Intent(context, DragActivity.class);
        context.startActivity(starter);
    }

    private List<SimpleData> mData;
    private DragAdapter mAdapter;
    private ItemTouchHelper mTouchHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        initData();
        initWidgets();
    }

    private void initData() {
        mData = new ArrayList<>();
        mData.add(new SimpleData("1", "First"));
        mData.add(new SimpleData("2", "Second"));
        mData.add(new SimpleData("3", "Third"));
        mData.add(new SimpleData("4", "Fourth"));
        mData.add(new SimpleData("5", "Fifth"));
        mData.add(new SimpleData("6", "Sixth"));
        mData.add(new SimpleData("7", "Seventh"));
        mData.add(new SimpleData("8", "Eighth"));
        mData.add(new SimpleData("9", "Ninth"));
        mData.add(new SimpleData("10", "Tenth"));
    }

    private void initWidgets() {
        ImageView ivTarget = (ImageView) findViewById(R.id.iv_target);
        ivTarget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    if (Build.VERSION.SDK_INT >= 24) {
                        v.startDragAndDrop(null, shadowBuilder, v, 0);
                    } else {
                        v.startDrag(null, shadowBuilder, v, 0);
                    }
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.vg_left).setOnDragListener(new OnViewGroupDragListener());
        findViewById(R.id.vg_right).setOnDragListener(new OnViewGroupDragListener());

        RecyclerView rvData = (RecyclerView) findViewById(R.id.rv_data);
        rvData.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DragAdapter(mData, this);
        rvData.setAdapter(mAdapter);

        ItemTouchHelper.Callback callbacks = new DragHelperCallbacks(this);
        mTouchHelper = new ItemTouchHelper(callbacks);
        mTouchHelper.attachToRecyclerView(rvData);

        final SwipeRefreshLayout srData = (SwipeRefreshLayout) findViewById(R.id.sr_data);
        srData.setColorSchemeResources(R.color.colorPrimary);
        srData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.notifyDataSetChanged();
                srData.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onItemMove(int formPosition, int toPosition) {
        Collections.swap(mData, formPosition ,toPosition);
        mAdapter.notifyItemMoved(formPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mData.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    private class OnViewGroupDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.INVISIBLE);

                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
                    break;
                }

                case DragEvent.ACTION_DRAG_ENTERED: {
                    v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
                    break;
                }

                case DragEvent.ACTION_DRAG_EXITED: {
                    v.setBackgroundResource(0);
                    break;
                }

                case DragEvent.ACTION_DROP: {
                    View view = (View) event.getLocalState();
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);

                    RelativeLayout container = (RelativeLayout) v;
                    container.addView(view, layoutParams);
                    break;
                }

                case DragEvent.ACTION_DRAG_ENDED: {
                    View view = (View) event.getLocalState();
                    view.setBackgroundResource(R.drawable.rect_grey_r4dp);
                    view.setVisibility(View.VISIBLE);

                    v.setBackgroundResource(0);
                    break;
                }
            }
            return true;
        }
    }
}
