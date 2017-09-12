package com.insightsuen.stayfoolish.ui.orm;

import android.content.Context;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.util.Log;

import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.library.helper.ToastHelper;
import com.insightsuen.stayfoolish.BR;
import com.insightsuen.stayfoolish.SFApplication;
import com.insightsuen.stayfoolish.model.DaoSession;
import com.insightsuen.stayfoolish.model.Note;
import com.insightsuen.stayfoolish.model.NoteDao;
import com.insightsuen.stayfoolish.model.NoteType;

import java.util.Date;

/**
 * Created by InSight Suen on 2017/9/12.
 */

public class OrmViewModel extends LifecycleViewModel {

    private static final String TAG = "OrmViewModel";

    private String mNoteTitle;
    private String mNoteContent;

    private NoteDao mNoteDao;

    @Override
    public void onStart(Context context) {
        super.onStart(context);
        ToastHelper.getInstance().init(context.getApplicationContext());

        DaoSession daoSession = ((SFApplication) context.getApplicationContext()).getDaoSession();
        mNoteDao = daoSession.getNoteDao();
    }

    public void onClickSaveNote() {
        if (TextUtils.isEmpty(mNoteTitle)) {
            ToastHelper.getInstance().show("Title is empty.");
            return;
        }
        if (TextUtils.isEmpty(mNoteContent)) {
            ToastHelper.getInstance().show("Content is empty.");
            return;
        }
        saveNote(mNoteTitle, mNoteContent);
        mNoteTitle = mNoteContent = "";
        notifyPropertyChanged(BR.noteTitle);
        notifyPropertyChanged(BR.noteContent);
    }

    @Bindable
    public String getNoteTitle() {
        return mNoteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        mNoteTitle = noteTitle;
    }

    @Bindable
    public String getNoteContent() {
        return mNoteContent;
    }

    public void setNoteContent(String noteContent) {
        mNoteContent = noteContent;
    }

    private void saveNote(String title, String content) {
        Note newNote = new Note();
        newNote.setTitle(title);
        newNote.setContent(content);
        newNote.setCreateTime(new Date());
        newNote.setType(NoteType.TEXT);
        mNoteDao.insert(newNote);
        Log.d(TAG, "saveNote: Inserted new note, ID: " + newNote.getId());
    }
}
