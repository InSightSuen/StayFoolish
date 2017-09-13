package com.insightsuen.stayfoolish.ui.rx;

import android.content.Context;

import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.stayfoolish.SFApplication;
import com.insightsuen.stayfoolish.model.DaoSession;
import com.insightsuen.stayfoolish.model.Note;
import com.insightsuen.stayfoolish.model.NoteDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by InSight Suen on 2017/9/13.
 */

public class RxViewModel extends LifecycleViewModel {

    //////////////////////////////
    // Data binding block start //
    //////////////////////////////

    public void onClickTest() {
        runRx();
    }

    ////////////////////////////
    // Data binding block end //
    ////////////////////////////

    private NoteDao mNoteDao;
    private Query<Note> mNoteQuery;
    private List<Note> mNotes;

    @Override
    public void onStart(Context context) {
        super.onStart(context);
        DaoSession daoSession = ((SFApplication) context.getApplicationContext()).getDaoSession();
        mNoteDao = daoSession.getNoteDao();
        mNoteQuery = mNoteDao.queryBuilder().orderAsc(NoteDao.Properties.Title).build();
    }

    private void runRx() {
        new Thread() {
            @Override
            public void run() {
                mNotes = mNoteQuery.list();
                for (Note note : mNotes) {
                    
                }
            }
        }.start();
    }

}
