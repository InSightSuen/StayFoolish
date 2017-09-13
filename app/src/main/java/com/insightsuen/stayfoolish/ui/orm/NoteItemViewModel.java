package com.insightsuen.stayfoolish.ui.orm;

import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.insightsuen.bindroid.viewmodel.BaseViewModel;
import com.insightsuen.stayfoolish.model.Note;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by InSight Suen on 2017/9/12.
 */

public class NoteItemViewModel extends BaseViewModel {

    private Parent mParent;
    private Note mNote;

    public NoteItemViewModel(Parent parent, @NonNull Note note) {
        mParent = parent;
        mNote = note;
    }

    @Bindable
    public String getTitle() {
        return mNote.getTitle();
    }

    @Bindable
    public String getContent() {
        return mNote.getContent();
    }

    @Bindable
    public String getCreateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(mNote.getCreateTime());
    }

    public interface Parent {

    }
}
