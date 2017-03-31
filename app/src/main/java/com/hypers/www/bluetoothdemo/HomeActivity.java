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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BluetoothLeAdvertiser listener = mBluetoothAdapter.getBluetoothLeAdvertiser();
                if (null == listener) {
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
                        listener.startAdvertising(createAdvSettings(true, 0), createFMPAdvertiseData(), mAdvCallback);
                    } catch (InterruptedException e) {
                        Log.v(TAG, "Fail to setup BleService");
                    }
                }
            }
        });
        boolean supportBLE = checkSupportBLE();
        if (supportBLE) {

            mBluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (null == mBluetoothAdapter) {
                Toast.makeText(this, "not support bluetooth_le", Toast.LENGTH_SHORT).show();
            } else if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLE);
            }
        }
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
        AdvertiseData adv = builder.build();
        return adv;
    }

    //发送广播的回调，onStartSuccess/onStartFailure很明显的两个Callback
    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        public void onStartSuccess(android.bluetooth.le.AdvertiseSettings settingsInEffect) {
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode() + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.d(TAG, "onStartSuccess, settingInEffect is null");
            }
        }

        public void onStartFailure(int errorCode) {
            Log.d(TAG, "onStartFailure errorCode=" + errorCode);
        }

        ;
    };


    private boolean checkSupportBLE() {
        if (!getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean mScanning = false;
    private int SCAN_PERIOD = 10000;


    private void scanLeDevice(final boolean enable) {


        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD); //10秒后停止搜索
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
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


}
