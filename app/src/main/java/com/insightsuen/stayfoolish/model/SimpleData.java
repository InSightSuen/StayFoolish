package com.insightsuen.stayfoolish.model;

/**
 * Created by InSight Suen on 2017/7/14.
 */
public class SimpleData {

    private String mTitle;
    private String mContent;
    private boolean mExtraInfo;

    public SimpleData(String title, String content) {
        mTitle = title;
        mContent = content;
        mExtraInfo = false;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public boolean isExtraInfo() {
        return mExtraInfo;
    }

    public void setExtraInfo(boolean extraInfo) {
        mExtraInfo = extraInfo;
    }
}
