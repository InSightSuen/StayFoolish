package com.insightsuen.stayfoolish.ui.main;

import android.view.View;

import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;
import com.insightsuen.stayfoolish.ui.aidl.AidlActivity;
import com.insightsuen.stayfoolish.ui.blur.BlurActivity;
import com.insightsuen.stayfoolish.ui.drag.DragActivity;
import com.insightsuen.stayfoolish.ui.handler.HandlerActivity;
import com.insightsuen.stayfoolish.ui.orm.OrmActivity;
import com.insightsuen.stayfoolish.ui.rx.RxActivity;
import com.insightsuen.stayfoolish.ui.scroll.NestedScrollActivity;
import com.insightsuen.stayfoolish.ui.style.UnifyStyleActivity;
import com.insightsuen.stayfoolish.ui.thread.ThreadActivity;
import com.insightsuen.stayfoolish.ui.toast.ToastActivity;
import com.insightsuen.stayfoolish.ui.widget.WidgetActivity;

/**
 * Created by InSight Suen on 2017/7/12.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    public void onClickDragAndDrop(View view) {
        DragActivity.start(this);
    }

    public void onClickToast(View view) {
        ToastActivity.start(this);
    }

    public void onClickBlur(View view) {
        BlurActivity.start(this);
    }

    public void onClickUnifyStyle(View view) {
        UnifyStyleActivity.start(this);
    }

    public void onClickWidget(View view) {
        WidgetActivity.start(this);
    }

    public void onClickThread(View view) {
        ThreadActivity.start(this);
    }

    public void onClickAidl(View view) {
        AidlActivity.start(this);
    }

    public void onClickHandler(View view) {
        HandlerActivity.start(this);
    }

    public void onClickOrm(View view) {
        OrmActivity.start(this);
    }

    public void onClickRx(View view) {
        RxActivity.start(this);
    }

    public void onClickNestedScroll(View view) {
        NestedScrollActivity.start(this);
    }

}
