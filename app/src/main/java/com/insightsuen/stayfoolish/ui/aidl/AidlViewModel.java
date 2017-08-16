package com.insightsuen.stayfoolish.ui.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.Bindable;
import android.os.IBinder;
import android.os.RemoteException;

import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.library.ILibAIDL;
import com.insightsuen.library.service.AidlService;
import com.insightsuen.stayfoolish.BR;

/**
 * Created by InSight Suen on 2017/8/11.
 */

public class AidlViewModel extends LifecycleViewModel {

    private int mValue1 = 100;
    private int mValue2 = 50;
    private int mResult = 0;

    private ServiceConnection mConnection;
    private ILibAIDL mLibAIDL;

    @Override
    public void onStart(Context context) {
        super.onStart(context);
        bindService(context);
    }

    @Override
    public void onStop() {
        unbindService(getContext());
        super.onStop();
    }

    @Bindable
    public String getValue1() {
        return mValue1 + "";
    }

    public void setValue1(String value1) {
        try {
            mValue1 = Integer.valueOf(value1);
        } catch (NumberFormatException e) {
            mValue1 = 0;
        }
    }

    @Bindable
    public String getValue2() {
        return mValue2 + "";
    }

    public void setValue2(String value2) {
        try {
            mValue2 = Integer.valueOf(value2);
        } catch (NumberFormatException e) {
            mValue2 = 0;
        }
    }

    @Bindable
    public String getResult() {
        return mResult + "";
    }

    public void onClickAdd() {
        try {
            mResult = mLibAIDL.add(mValue1, mValue2);
            notifyPropertyChanged(BR.result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindService(Context context) {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mLibAIDL = ILibAIDL.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mLibAIDL = null;
            }
        };
        Intent intent = new Intent(context, AidlService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService(Context context) {
        context.unbindService(mConnection);
    }

}
