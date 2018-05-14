package com.insightsuen.library.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.util.Map;

public final class IdentifyUtils {

    public static final String UNKNOWN = "unknown";

    private IdentifyUtils() {
        // no instance
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        String androidId;
        try {
            androidId = Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            androidId = null;
        }
        if (TextUtils.isEmpty(androidId) || TextUtils.equals(androidId, "9774d56d682e549c")) {
            androidId = "unknown";
        }
        return androidId;
    }

    @SuppressLint("HardwareIds")
    public static String getBuildSerial(Context context) {
        String buildSerial = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            buildSerial = Build.SERIAL;
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                buildSerial = Build.getSerial();
            } else {
                buildSerial = UNKNOWN;
            }
        }
        return buildSerial;
    }

    @SuppressLint("HardwareIds")
    public static String getWifiMacAddress(Context context) {
        String macAddress = null;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            WifiManager wifiManager = ((WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE));
            if (wifiManager != null) {
                WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null) {
                    macAddress = connectionInfo.getMacAddress();
                }
            }
        }
        if (TextUtils.isEmpty(macAddress) || TextUtils.equals(macAddress, "02:00:00:00:00:00")) {
            return "unknown";
        } else {
            return macAddress;
        }
    }

    @SuppressLint("HardwareIds")
    public static String getBluetoothMacAddress(Context context) {
        String macAddress = null;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null) {
                BluetoothAdapter adapter = bluetoothManager.getAdapter();
                if (adapter != null) {
                    macAddress = adapter.getAddress();
                }
            }
        }
        if (TextUtils.isEmpty(macAddress) || TextUtils.equals(macAddress, "02:00:00:00:00:00")) {
            return "unknown";
        } else {
            return macAddress;
        }
    }

    @SuppressLint("HardwareIds")
    public static Map<String, String> getPhoneInfoMap(Context context) {
        Map<String, String> infoMap = new ArrayMap<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /* Get sim card slot count */
                    int slotCountMax;
                    SubscriptionManager subscriptionManager =
                            (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    if (subscriptionManager != null) {
                        slotCountMax = subscriptionManager.getActiveSubscriptionInfoCountMax();
                    } else {
                        slotCountMax = 2; // assume have 2 sim card slot max.
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String imei;
                        for (int i = 0; i < slotCountMax; i++) {
                            imei = telephonyManager.getImei(i);
                            if (!TextUtils.isEmpty(imei)) {
                                infoMap.put("IMEI_" + i, imei);
                            }
                        }
                        String meid;
                        for (int i = 0; i < slotCountMax; i++) {
                            meid = telephonyManager.getMeid(i);
                            if (!TextUtils.isEmpty(meid)) {
                                infoMap.put("MEID_" + i, meid);
                            }
                        }
                    } else {
                        String deviceId;
                        for (int i = 0; i < slotCountMax; i++) {
                            deviceId = telephonyManager.getDeviceId(i);
                            if (!TextUtils.isEmpty(deviceId)) {
                                infoMap.put("DeviceID_" + i, deviceId);
                            }
                        }
                    }
                } else {
                    String deviceId = telephonyManager.getDeviceId();
                    infoMap.put("DeviceID", deviceId);
                }
            }
        }
        return infoMap;
    }
}
