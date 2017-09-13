package com.insightsuen.stayfoolish.ui.orm;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.insightsuen.bindroid.component.recyclerview.BindAdapter;
import com.insightsuen.bindroid.component.recyclerview.DiffCallBacks;
import com.insightsuen.bindroid.component.recyclerview.RecyclerViewBindable;
import com.insightsuen.bindroid.component.recyclerview.RecyclerViewBinder;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.library.helper.ToastHelper;
import com.insightsuen.stayfoolish.BR;
import com.insightsuen.stayfoolish.SFApplication;
import com.insightsuen.stayfoolish.model.DaoSession;
import com.insightsuen.stayfoolish.model.Note;
import com.insightsuen.stayfoolish.model.NoteDao;
import com.insightsuen.stayfoolish.model.NoteType;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by InSight Suen on 2017/9/12.
 */

public class OrmViewModel extends LifecycleViewModel implements NoteItemViewModel.Parent,
        RecyclerViewBindable {

    //////////////////////////////
    // Data binding block start //
    //////////////////////////////

    public final ObservableBoolean isLoading = new ObservableBoolean(false);

    public void bind(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        if (adapter instanceof BindAdapter) {
            DiffCallBacks<NoteItemViewModel> callBacks = new DiffCallBacks<NoteItemViewModel>() {
                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    NoteItemViewModel oldItem = mOld.get(oldItemPosition);
                    NoteItemViewModel newItem = mNew.get(newItemPosition);
                    return oldItem != null && newItem != null && oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    NoteItemViewModel oldItem = mOld.get(oldItemPosition);
                    NoteItemViewModel newItem = mNew.get(newItemPosition);
                    return TextUtils.equals(oldItem.getTitle(), newItem.getTitle())
                            && TextUtils.equals(oldItem.getContent(), newItem.getContent());
                }
            };
            //noinspection unchecked
            mBinder = new RecyclerViewBinder<>((BindAdapter<NoteItemViewModel>) adapter, callBacks);
            mBinder.setDetectMoves(true);
            mBinder.enableDispatch(new RecyclerViewBinder.UpdateDataCallbacks() {
                @Override
                public void onUpdateStart() {
                    isLoading.set(true);
                }

                @Override
                public void onUpdateFinished() {
                    isLoading.set(false);
                }
            });
            refreshItemViewModelList();
        }
    }

    public void onRefreshNotes(Context context) {
        refreshItemViewModelList();
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

    ////////////////////////////
    // Data binding block end //
    ////////////////////////////

    private static final String TAG = "OrmViewModel";

    private String mNoteTitle;
    private String mNoteContent;

    private NoteDao mNoteDao;
    private Query<Note> mNoteQuery;
    private List<Note> mNotes;
    private RecyclerViewBinder<NoteItemViewModel> mBinder;

    OrmViewModel() {
        mNotes = new ArrayList<>();
    }

    @Override
    public void onStart(Context context) {
        super.onStart(context);
        ToastHelper.getInstance().init(context.getApplicationContext());

        DaoSession daoSession = ((SFApplication) context.getApplicationContext()).getDaoSession();
        mNoteDao = daoSession.getNoteDao();
        mNoteQuery = mNoteDao.queryBuilder().orderAsc(NoteDao.Properties.Title).build();

    }

    private void refreshItemViewModelList() {
        mNotes = mNoteQuery.list();
        List<NoteItemViewModel> viewModelList = new ArrayList<>();
        for (Note item : mNotes) {
            viewModelList.add(new NoteItemViewModel(this, item));
        }
        if (mBinder != null) {
            mBinder.onUpdateData(viewModelList);
        }
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
