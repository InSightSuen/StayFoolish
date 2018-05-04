package com.insightsuen.stayfoolish;

import android.app.Application;

import com.insightsuen.library.helper.ToastHelper;
import com.insightsuen.stayfoolish.model.DaoMaster;
import com.insightsuen.stayfoolish.model.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by InSight Suen on 2017/9/12.
 */

public class SFApplication extends Application {

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();

        ToastHelper.getInstance().init(this);
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}
