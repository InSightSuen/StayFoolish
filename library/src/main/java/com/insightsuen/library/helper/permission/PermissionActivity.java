package com.insightsuen.library.helper.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Insight Suen on 2017/12/7.
 */

public class PermissionActivity extends Activity {

    private static final String EXTRA_PERMISSION_REQUESTS = "PermissionRequests";
    private static final int CODE_REQUEST_PERMISSION = 3515;

    private List<PermissionRequest> mPermissionRequests;
    public static void start(Context context, List<PermissionRequest> requests) {
        Intent starter = new Intent(context, PermissionActivity.class);
        starter.putParcelableArrayListExtra(EXTRA_PERMISSION_REQUESTS, new ArrayList<Parcelable>(requests));
        context.startActivity(starter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (getIntent() != null) {
            mPermissionRequests = getIntent().getParcelableArrayListExtra(EXTRA_PERMISSION_REQUESTS);
        }
        if (mPermissionRequests != null && mPermissionRequests.size() > 0) {
            startRequest();
        } else {
            finish();
        }
    }

    private void startRequest() {
        boolean hasRequest = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> requestPermissions = new ArrayList<>();
            for (int i = 0; i < mPermissionRequests.size(); i++) {
                PermissionRequest request = mPermissionRequests.get(i);
                requestPermissions.clear();
                for (String permission : request.getPermissions()) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions.add(permission);
                    }
                }
                if (!requestPermissions.isEmpty()) {
                    hasRequest = true;
                    requestPermissions(
                            requestPermissions.toArray(new String[requestPermissions.size()]),
                            CODE_REQUEST_PERMISSION + i);
                }
            }
        }

        if (!hasRequest) {
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int requestIndex = requestCode - CODE_REQUEST_PERMISSION;
        if (requestIndex >= 0 && requestIndex < mPermissionRequests.size()) {
            checkResult(mPermissionRequests.get(requestIndex));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkResult(PermissionRequest request) {
        List<String> retryPermissions = new ArrayList<>();
        boolean finished = true;
        for (String permission : request.getPermissions()) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    retryPermissions.add(permission);
                    finished = false;
                }
            }
        }
        request.setFinished(finished);

        if (!retryPermissions.isEmpty()) {
            showRationale(request);
        }

        checkOrSetResult();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkOrSetResult() {
        boolean allFinished = true;
        for (PermissionRequest request : mPermissionRequests) {
            if (!request.isFinished()) {
                allFinished = false;
                break;
            }
        }

        if (allFinished) {
            List<String> grantedPermissions = new ArrayList<>();
            List<String> deniedPermissions = new ArrayList<>();
            for (PermissionRequest request : mPermissionRequests) {
                for (String permission : request.getPermissions()) {
                    if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissions.add(permission);
                    } else {
                        deniedPermissions.add(permission);
                    }
                }
            }
            PermissionHelper.getInstance().onRequestResult(
                    mPermissionRequests.size() == grantedPermissions.size(),
                    grantedPermissions.toArray(new String[grantedPermissions.size()]),
                    deniedPermissions.toArray(new String[deniedPermissions.size()]));
            finish();
        }
    }

    private void showRationale(final PermissionRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(null)
                .setMessage(request.getRationale())
                .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(request.getPermissions(),
                                mPermissionRequests.indexOf(request) + CODE_REQUEST_PERMISSION);
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.setFinished(true);
                        checkOrSetResult();
                    }
                })
                .show();
    }
}
