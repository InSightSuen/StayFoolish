package com.insightsuen.stayfoolish.ui.toast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.insightsuen.library.helper.ToastHelper;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

/**
 * Created by InSight Suen on 2017/7/28.
 */

public class ToastActivity extends BaseActivity implements View.OnClickListener {

    private ToastHelper mToastHelper;

    private EditText etMsg;

    private RadioGroup rgGravityHorizontal;
    private RadioGroup rgGravityVertical;
    private int mGravityVertical;
    private int mGravityHorizontal;

    private SeekBar sbXOffSet;
    private SeekBar sbYOffset;
    private int mXOffset = 0;
    private int mYOffset = 0;

    public static void start(Context context) {
        Intent starter = new Intent(context, ToastActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_toast;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
        initData();
        initWidgets();
    }

    private void initData() {
        mToastHelper = ToastHelper.getInstance();
        mToastHelper.init(getApplicationContext());
        mToastHelper.setView(R.layout.toast_customer, R.id.tv_msg);
        mXOffset = mToastHelper.getXOffset();
        mYOffset = mToastHelper.getYOffset();
    }

    private void initWidgets() {
        etMsg = (EditText) findViewById(R.id.et_msg);

        rgGravityHorizontal = (RadioGroup) findViewById(R.id.rg_horizontal);
        for (int i = 0; i < rgGravityHorizontal.getChildCount(); i++) {
            View radioButton = rgGravityHorizontal.getChildAt(i);
            if (radioButton instanceof RadioButton) {
                radioButton.setOnClickListener(this);
            }
        }
        rgGravityVertical = (RadioGroup) findViewById(R.id.rg_vertical);
        for (int i = 0; i < rgGravityVertical.getChildCount(); i++) {
            View radioButton = rgGravityVertical.getChildAt(i);
            if (radioButton instanceof RadioButton) {
                radioButton.setOnClickListener(this);
            }
        }

        sbXOffSet = (SeekBar) findViewById(R.id.sb_x_offset);
        sbXOffSet.setMax(200);
        sbXOffSet.setProgress(mXOffset);
        sbXOffSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mXOffset = progress;
                    mToastHelper.setGravity(mGravityVertical | mGravityHorizontal, mXOffset, mYOffset);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbYOffset = (SeekBar) findViewById(R.id.sb_y_offset);
        sbYOffset.setMax(200);
        sbYOffset.setProgress(mYOffset);
        sbYOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mYOffset = progress;
                    mToastHelper.setGravity(mGravityVertical | mGravityHorizontal, mXOffset, mYOffset);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void onClickToast(View view) {
        mToastHelper.show(etMsg.getText());
    }

    public void onClickUseSystemDefaultGravity(View view) {
        mToastHelper.userDefaultGravity();
        rgGravityVertical.clearCheck();
        rgGravityHorizontal.clearCheck();
        sbXOffSet.setProgress(mXOffset = mToastHelper.getXOffset());
        sbYOffset.setProgress(mYOffset = mToastHelper.getYOffset());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_left:
                mGravityHorizontal = Gravity.LEFT;
                break;

            case R.id.rb_right:
                mGravityHorizontal = Gravity.RIGHT;
                break;

            case R.id.rb_center_horizontal:
                mGravityHorizontal = Gravity.CENTER_HORIZONTAL;
                break;

            case R.id.rb_bottom:
                mGravityVertical = Gravity.BOTTOM;
                break;

            case R.id.rb_top:
                mGravityVertical = Gravity.TOP;
                break;

            case R.id.rb_center_vertical:
                mGravityVertical = Gravity.CENTER_VERTICAL;
                break;

            default:
                return;
        }
        mToastHelper.setGravity(mGravityVertical | mGravityHorizontal);
    }
}
