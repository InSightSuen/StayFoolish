package com.insightsuen.library.helper.permission;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Insight Suen on 2017/12/11.
 * 权限请求
 */

public class PermissionRequest implements Parcelable {

    private final String[] mPermissions;
    private final String mRationale;
    private final boolean mEssential;
    private boolean mFinished;

    public PermissionRequest(String permission) {
        this(permission, null);
    }

    public PermissionRequest(String permission, String rationale) {
        this(new String[]{permission}, rationale);
    }

    public PermissionRequest(String[] permissions, String rationale) {
        this(permissions, rationale, true);
    }

    public PermissionRequest(String[] permissions, String rationale, boolean essential) {
        mPermissions = permissions;
        mRationale = rationale;
        mEssential = essential;
        mFinished = false;
    }

    public String[] getPermissions() {
        return mPermissions;
    }

    public String getRationale() {
        return mRationale;
    }

    public boolean isEssential() {
        return mEssential;
    }

    public boolean isFinished() {
        return mFinished;
    }

    public void setFinished(boolean finished) {
        mFinished = finished;
    }

    protected PermissionRequest(Parcel in) {
        mPermissions = in.createStringArray();
        mRationale = in.readString();
        mEssential = in.readByte() != 0;
        mFinished = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(mPermissions);
        dest.writeString(mRationale);
        dest.writeByte((byte) (mEssential ? 1 : 0));
        dest.writeByte((byte) (mFinished ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PermissionRequest> CREATOR = new Creator<PermissionRequest>() {
        @Override
        public PermissionRequest createFromParcel(Parcel in) {
            return new PermissionRequest(in);
        }

        @Override
        public PermissionRequest[] newArray(int size) {
            return new PermissionRequest[size];
        }
    };

    public static class Builder {

        private List<String> mPermissions;
        private String mRationale = null;
        private boolean mEssential = true;

        public Builder(@NonNull String... permissions) {
            mPermissions = new ArrayList<>();
            addPermissions(permissions);
        }

        public Builder(@NonNull List<String> permissions) {
            mPermissions = new ArrayList<>();
            addPermissions(permissions);
        }

        public Builder addPermission(String permission) {
            checkPermission(permission);
            mPermissions.add(permission);
            return this;
        }

        public Builder addPermissions(String... permissions) {
            for (String permission: permissions) {
                checkPermission(permission);
                mPermissions.add(permission);
            }
            return this;
        }

        public Builder addPermissions(List<String> permissions) {
            for (String permission: permissions) {
                checkPermission(permission);
                mPermissions.add(permission);
            }
            return this;
        }

        public Builder setRationale(@Nullable String rationale) {
            mRationale = rationale;
            return this;
        }

        public Builder setEssential(boolean essential) {
            mEssential = essential;
            return this;
        }

        public PermissionRequest build() {
            String[] permissions = mPermissions.toArray(new String[mPermissions.size()]);
            String rationale = mRationale;
            if (rationale == null) {
                rationale = mEssential ? "应用正常运行必须允许权限" : "";
            }
            return new PermissionRequest(permissions, rationale, mEssential);
        }

        private void checkPermission(String permission) {
            if (TextUtils.isEmpty(permission)) {
                throw new IllegalArgumentException("permission is null");
            }
        }
    }
}
