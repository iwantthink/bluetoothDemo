package com.hypers.www.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BLE = 111;
    public static final String BLE_SERVICE_CLOSED = "蓝牙外设服务已关闭";
    private Switch mSwitchBle;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private Activity mActivity = this;
    private MockServerCallBack mMockServerCallBack;
    private BluetoothGattServer mGattServer;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;
    private TextView mTvBleState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSwitchBle = (Switch) findViewById(R.id.switch_ble);
        mTvBleState = (TextView) findViewById(R.id.tv_state);
        initBle();
        mSwitchBle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openBle();
                } else {
                    closeBle();
                }
            }
        });

    }

    private void initBle() {
        //判断是否支持ble
        boolean supportBLE = checkSupportBLE();
        if (supportBLE) {
            mBluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            //俩种获取bluetoothAdapter 的方式
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (null == mBluetoothAdapter) {
                Toast.makeText(this, "not support bluetooth", Toast.LENGTH_SHORT).show();
            } else if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled()) {
                //开启蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLE);
            } else if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled()) {
                switchTrue();
                openBle();
            }
        }
    }

    private void openBle() {
        //getBluetoothAdvertiser也是会调用下面的方法去判断是否支持ble
//                boolean isSupported = mBluetoothAdapter.isMultipleAdvertisementSupported();
        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (null == mBluetoothAdvertiser) {
            Toast.makeText(HomeActivity.this, "not support bluetoothle", Toast.LENGTH_SHORT).show();
            switchFalse();
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
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode() + " timeout=" + settingsInEffect.getTimeout());
                switchTrue();
            } else {
                Log.d(TAG, "onStartSuccess, settingInEffect is null");
                switchFalse();
            }
        }

        public void onStartFailure(int errorCode) {
            Log.d(TAG, "onStartFailure errorCode=" + errorCode);
            switchFalse();
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
        return getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? true : false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
                    switchTrue();
                } else {
                    Toast.makeText(this, "蓝牙未启用", Toast.LENGTH_SHORT).show();
                    switchFalse();
                }
                break;
        }
    }

    private void switchTrue() {
        mSwitchBle.setChecked(true);
        mTvBleState.setText("蓝牙外设服务已开启");
    }

    private void switchFalse() {
        mSwitchBle.setChecked(false);
        mTvBleState.setText(BLE_SERVICE_CLOSED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSwitchBle.setChecked(false);
    }

    private void closeBle() {
        //关闭BluetoothLeAdvertiser，BluetoothGattServer
        if (mBluetoothAdvertiser != null) {
            mBluetoothAdvertiser.stopAdvertising(mAdvCallback);
            mBluetoothAdvertiser = null;
        }

//        if (mBluetoothAdapter != null) {
//            mBluetoothAdapter = null;
//        }

        if (mGattServer != null) {
            mGattServer.clearServices();
            mGattServer.close();
        }
        switchFalse();
    }
}
