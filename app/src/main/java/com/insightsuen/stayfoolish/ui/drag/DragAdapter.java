package com.insightsuen.stayfoolish.ui.drag;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.model.SimpleData;

import java.util.List;

/**
 * Created by InSight Suen on 2017/7/14.
 * Drag and drop Recycler adapter
 */
class DragAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SimpleData> mData;
    private OnStartDragListener mStartDragListener;

    public DragAdapter(List<SimpleData> data, OnStartDragListener startDragListener) {
        mData = data;
        mStartDragListener = startDragListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_data, parent, false);
        return new SimpleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SimpleViewHolder) {
            ((SimpleViewHolder) holder).onBind(mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvContent;
        private ViewGroup vgContainer;

        SimpleViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            itemView.findViewById(R.id.ib_drag).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (mStartDragListener != null) {
                            mStartDragListener.onStartDrag(SimpleViewHolder.this);
                            return true;
                        }
                    }
                    return false;
                }
            });

            vgContainer = (ViewGroup) itemView.findViewById(R.id.vg_container);
        }

        private void onBind(SimpleData sd) {
            tvTitle.setText(sd.getTitle());
            tvContent.setText(sd.getContent());
            vgContainer.setOnDragListener(new OnItemDragListener(sd));
            View target = itemView.findViewById(R.id.iv_target);
            if (sd.isExtraInfo()) {
                if (target == null) {
                    LayoutInflater.from(itemView.getContext()).inflate(R.layout.merge_drag_target, vgContainer, true);
                    target = itemView.findViewById(R.id.iv_target);
                    target.setOnTouchListener(new View.OnTouchListener() {
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
                }
            } else {
                if (target != null) {
                    vgContainer.removeView(target);
                }
            }
        }
    }

    private class OnItemDragListener implements View.OnDragListener {

        private SimpleData mData;
        private boolean mStartValue;

        OnItemDragListener(SimpleData data) {
            mData = data;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.INVISIBLE);

                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));

                    mStartValue = mData.isExtraInfo();
                    break;
                }

                case DragEvent.ACTION_DRAG_ENTERED: {
                    v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
                    mData.setExtraInfo(true);
                    break;
                }

                case DragEvent.ACTION_DRAG_EXITED: {
                    v.setBackgroundResource(0);
                    mData.setExtraInfo(false);
                    break;
                }

                case DragEvent.ACTION_DROP: {
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);

                    RelativeLayout container = (RelativeLayout) v;
                    container.addView(view);
                    break;
                }

                case DragEvent.ACTION_DRAG_ENDED: {
                    View view = (View) event.getLocalState();
                    view.setBackgroundResource(R.drawable.rect_grey_r4dp);
                    view.setVisibility(View.VISIBLE);

                    v.setBackgroundResource(0);

                    if (!event.getResult() && mStartValue) {
                        mData.setExtraInfo(true);
                    }
                    break;
                }

                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
            }
            return true;
        }
    }
}
