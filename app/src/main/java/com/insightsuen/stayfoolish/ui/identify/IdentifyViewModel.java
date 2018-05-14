package com.insightsuen.stayfoolish.ui.identify;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.ObservableField;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.library.util.IdentifyUtils;

import java.util.Map;

public class IdentifyViewModel extends LifecycleViewModel {

    private static final String UNKNOWN = IdentifyUtils.UNKNOWN;

    public final ObservableField<String> androidId = new ObservableField<>(UNKNOWN);
    public final ObservableField<String> buildSerial = new ObservableField<>(UNKNOWN);

    public final ObservableField<String> wifiMacAddress = new ObservableField<>(UNKNOWN);
    public final ObservableField<String> bluetoothMacAddress = new ObservableField<>(UNKNOWN);

    public final ObservableField<String> imei = new ObservableField<>(UNKNOWN);
    public final ObservableField<String> meid = new ObservableField<>(UNKNOWN);
    public final ObservableField<String> deviceId = new ObservableField<>(UNKNOWN);

    @Override

    public void onStart(Context context) {
        super.onStart(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity) context),
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE},
                    0);
        }
        loadHardwareIds(context);
    }

    private void loadHardwareIds(Context context) {
        androidId.set(IdentifyUtils.getAndroidId(context));
        buildSerial.set(IdentifyUtils.getBuildSerial(context));
        wifiMacAddress.set(IdentifyUtils.getWifiMacAddress(context));
        bluetoothMacAddress.set(IdentifyUtils.getBluetoothMacAddress(context));

        Map<String, String> phoneInfoMap = IdentifyUtils.getPhoneInfoMap(context);
        for (Map.Entry<String, String> info : phoneInfoMap.entrySet()) {
            String key = info.getKey();
            String value = info.getValue();
            if (key.startsWith("IMEI")) {
                imei.set(value);
            } else if (key.startsWith("MEID")) {
                meid.set(value);
            } else if (key.startsWith("DeviceID")) {
                deviceId.set(value);
            }
        }
    }

}
