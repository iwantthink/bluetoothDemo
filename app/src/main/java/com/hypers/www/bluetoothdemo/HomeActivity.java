package com.hypers.www.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BLE = 111;
    private Button mBtnOpen;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    private static final String TAG = HomeActivity.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private Activity mActivity = this;
    private MockServerCallBack mMockServerCallBack;
    private BluetoothGattServer mGattServer;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBtnOpen = (Button) findViewById(R.id.btn_open);

        boolean supportBLE = checkSupportBLE();
        if (supportBLE) {

            mBluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            //俩种获取bluetoothAdapter 的方式
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (null == mBluetoothAdapter) {
                Toast.makeText(this, "not support bluetooth", Toast.LENGTH_SHORT).show();
            } else if (!mBluetoothAdapter.isEnabled()) {
                //开启蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLE);
            }
        }

        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getBluetoothAdvertiser也是会调用下面的方法去判断是否支持ble
//                boolean isSupported = mBluetoothAdapter.isMultipleAdvertisementSupported();
                mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
                if (null == mBluetoothAdvertiser) {
                    Toast.makeText(HomeActivity.this, "not support bluetoothle", Toast.LENGTH_SHORT).show();
                } else {
                    mMockServerCallBack = new MockServerCallBack();
                    //打开BluetoothGattServer
                    mGattServer = mBluetoothManager.openGattServer(mActivity, mMockServerCallBack);
                    if (mGattServer == null) {
                        Log.d(TAG, "gatt is null");
                    }
                    try {
                        mMockServerCallBack.setupServices(mGattServer);
                        //创建BLE Adevertising并且广播
                        mBluetoothAdvertiser.startAdvertising(createAdvSettings(true, 0),
                                createFMPAdvertiseData(),
                                mAdvCallback);
                    } catch (InterruptedException e) {
                        Log.v(TAG, "Fail to setup BleService");
                    }
                }
            }
        });

    }

    public static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        //设置广播的模式,应该是跟功耗相关
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setConnectable(connectable);
        builder.setTimeout(timeoutMillis);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        return builder.build();
    }

    //设置一下FMP广播数据
    public static AdvertiseData createFMPAdvertiseData() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeDeviceName(true);
        builder.addServiceUuid(ParcelUuid.fromString("0000fff2-0000-1000-8000-00805f9b34fb"));
        AdvertiseData adv = builder.build();
        return adv;
    }

    //发送广播的回调，onStartSuccess/onStartFailure很明显的两个Callback
    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        public void onStartSuccess(android.bluetooth.le.AdvertiseSettings settingsInEffect) {
            if (settingsInEffect != null) {
                Toast.makeText(mActivity, "start success", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode() + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.d(TAG, "onStartSuccess, settingInEffect is null");
            }
        }

        public void onStartFailure(int errorCode) {
            Toast.makeText(mActivity, "start failure", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onStartFailure errorCode=" + errorCode);
            switch (errorCode) {

                case 1:
                    Log.d(TAG, "errorCode1 ADVERTISE_FAILED_DATA_TOO_LARGE");
                    break;
                case 2:
                    Log.d(TAG, "errorCode2 ADVERTISE_FAILED_TOO_MANY_ADVERTISERS");
                    break;
                case 3:
                    Log.d(TAG, "errorCode3 ADVERTISE_FAILED_ALREADY_STARTED");
                    break;
                case 4:
                    Log.d(TAG, "errorCode4 ADVERTISE_FAILED_INTERNAL_ERROR");
                    break;
                case 5:
                    Log.d(TAG, "errorCode5 ADVERTISE_FAILED_FEATURE_UNSUPPORTED");
                    break;
            }
        }
    };


    private boolean checkSupportBLE() {
        if (!getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        } else {
            return true;
        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //在这里可以把搜索到的设备保存起来
                    device.getName();
                    //获取蓝牙设备名字
                    device.getAddress();
                    //获取蓝牙设备mac地址
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLE:
                Log.d("HomeActivity", "resultCode:" + resultCode);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "蓝牙未启用", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭BluetoothLeAdvertiser，BluetoothAdapter，BluetoothGattServer
        if (mBluetoothAdvertiser != null) {
            mBluetoothAdvertiser.stopAdvertising(mAdvCallback);
            mBluetoothAdvertiser = null;
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter = null;
        }

        if (mGattServer != null) {
            mGattServer.clearServices();
            mGattServer.close();
        }
    }
}
