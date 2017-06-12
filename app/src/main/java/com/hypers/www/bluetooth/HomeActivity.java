package com.hypers.www.bluetooth;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.hmt.analytics.viewexplosion.ExplosionView;
import com.hmt.analytics.viewexplosion.factory.FlyawayFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class HomeActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BLE = 111;
    public static final String BLE_SERVICE_CLOSED = "蓝牙外设服务已关闭";
    private static final String TAG = HomeActivity.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private Activity mActivity = this;
    private MockServerCallBack mMockServerCallBack;
    private BluetoothGattServer mGattServer;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;
    private ImageView mIvAvatar;
    private ImageView mIvShare;
    private static ExplosionView sExplosionView;
    private boolean sIsFirstEnter = true;

    public static void start(Activity context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        initView();
        initBle();
        initListener();
    }

    private void initListener() {

        sExplosionView = initExplo();
        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sExplosionView.getMode() == ExplosionView.MODE.ANNULUS) {
                    openBle();
                } else {
                    closeBle();
                }
            }
        });

        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UMWeb web = new UMWeb("http://www.hypers.com/");
                web.setTitle("我正在使用Reno");
                web.setThumb(new UMImage(HomeActivity.this, R.mipmap.ic_launcher));
                web.setDescription("快来下载吧");

                new ShareAction(HomeActivity.this).withMedia(web)
                        .setDisplayList(SHARE_MEDIA.SMS, SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN)
                        .setCallback(new UMShareListener() {
                            @Override
                            public void onStart(SHARE_MEDIA platform) {
                                //分享开始的回调
                            }

                            @Override
                            public void onResult(SHARE_MEDIA platform) {
                                Toast.makeText(HomeActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(SHARE_MEDIA platform, Throwable t) {
                                Toast.makeText(HomeActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
                                if (t != null) {
                                    Log.e("throw", "throw:" + t.getMessage());
                                }
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA platform) {
                                Toast.makeText(HomeActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
                            }
                        }).open();
            }
        });
    }

    private void initView() {
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged");
        Log.d(TAG, "sIsFirstEnter:" + sIsFirstEnter);
        if (sIsFirstEnter) {
            sExplosionView.setMode(ExplosionView.MODE.ANNULUS);
            sExplosionView.explode(mIvAvatar);
            sIsFirstEnter = false;
        }

    }

    @NonNull
    private ExplosionView initExplo() {
        final ExplosionView explosionView = new ExplosionView(this, new FlyawayFactory());
        explosionView.setHideStatusBar(true);
        explosionView.setSrc(R.mipmap.logo_pink);
        return explosionView;
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
                Toast.makeText(this, "请打开蓝牙功能", Toast.LENGTH_SHORT).show();
            } else if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled()) {
                //开启蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLE);
            }
//            else if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled()) {
//                switchTrue();
//                openBle();
//            }
        } else {
            Toast.makeText(mActivity, "设备不支持BLE功能", Toast.LENGTH_SHORT).show();
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
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
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
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
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
        if (null != sExplosionView && null != mIvAvatar) {
            sExplosionView.setMode(ExplosionView.MODE.EXPLOSION);
            sExplosionView.explode(mIvAvatar);
        }
    }

    private void switchFalse() {
        if (null != sExplosionView && null != mIvAvatar) {
            sExplosionView.setMode(ExplosionView.MODE.ANNULUS);
            sExplosionView.explode(mIvAvatar);
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        closeBle();
    }
}
