package com.insightsuen.library.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * A simple non-UI Fragment that stores a Object and is retained over configuration
 * changes.
 */
public class RetainFragment<T> extends Fragment {

    private T mObject;

    public static <T> RetainFragment<T> newInstance(T object) {
        Bundle args = new Bundle();
        RetainFragment<T> fragment = new RetainFragment<>();
        fragment.setArguments(args);
        fragment.setObject(object);
        return fragment;
    }

    public RetainFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // make sure this Fragment is retained over a configuration change
        setRetainInstance(true);
    }

    public void setObject(T object) {
        mObject = object;
    }

    public T getObject() {
        return mObject;
    }
}
