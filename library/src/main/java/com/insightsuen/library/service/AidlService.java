package com.insightsuen.library.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.insightsuen.library.ILibAIDL;

/**
 * Created by InSight Suen on 2017/8/11.
 */

public class AidlService extends Service {

    private IBinder mBinder = new ILibAIDL.Stub() {

        @Override
        public int add(int value1, int value2) throws RemoteException {
            return value1 + value2;
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
