package com.insightsuen.stayfoolish.model;

import android.database.Cursor;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by InSight Suen on 2017/7/21.
 */
public class ImageInfo {

    public static final String[] PROJECTION = {
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.SIZE,
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE,
            MediaStore.Images.ImageColumns.ORIENTATION,
            MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC};

    private long mId;
    private String mPath;
    private String mName;
    private long mDateToken;
    private long mSize;
    private String mLatitude;
    private String mLongitude;
    private int mOrientation;
    private int mMiniThumbMagic;

    public ImageInfo(long id, String path) {
        mId = id;
        mPath = path;
    }

    public ImageInfo(Cursor cursor) {
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getColumnName(i)) {
                case MediaStore.Images.ImageColumns._ID:
                    mId = cursor.getLong(i);
                    break;

                case MediaStore.Images.ImageColumns.DATA:
                    mPath = cursor.getString(i);
                    break;

                case MediaStore.Images.ImageColumns.DISPLAY_NAME:
                    mName = cursor.getString(i);
                    break;

                case MediaStore.Images.ImageColumns.DATE_TAKEN:
                    mDateToken = cursor.getLong(i);
                    break;

                case MediaStore.Images.ImageColumns.SIZE:
                    mSize = cursor.getLong(i);
                    break;

                case MediaStore.Images.ImageColumns.LATITUDE:
                    mLatitude = cursor.getString(i);
                    break;

                case MediaStore.Images.ImageColumns.LONGITUDE:
                    mLongitude = cursor.getString(i);
                    break;

                case MediaStore.Images.ImageColumns.ORIENTATION:
                    mOrientation = cursor.getInt(i);
                    break;

                case MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC:
                    mMiniThumbMagic = cursor.getInt(i);
                    break;

            }
        }
    }

    public String getPath() {
        return mPath;
    }

    public String getName() {
        return mName;
    }

    public long getDateToken() {
        return mDateToken;
    }

    public String getDateTokenString() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            return sdf.format(new Date(mDateToken));
        } catch (Exception e) {
            return "";
        }
    }

    public long getSize() {
        return mSize;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public int getMiniThumbMagic() {
        return mMiniThumbMagic;
    }
}
