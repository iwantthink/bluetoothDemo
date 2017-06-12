package com.hypers.www.bluetooth.home.p;

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
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hmt.analytics.viewexplosion.ExplosionView;
import com.hmt.analytics.viewexplosion.factory.FlyawayFactory;
import com.hypers.www.bluetooth.MockServerCallBack;
import com.hypers.www.bluetooth.R;
import com.hypers.www.bluetooth.home.v.IHomeView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by renbo on 2017/6/12.
 */

public class HomePresent implements IHomePresent {

    private IHomeView mHomeView;
    public static final int REQUEST_ENABLE_BLE = 111;
    public static final String BLE_SERVICE_CLOSED = "蓝牙外设服务已关闭";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private MockServerCallBack mMockServerCallBack;
    private BluetoothGattServer mGattServer;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;
    private boolean sIsFirstEnter = true;
    private static ExplosionView sExplosionView;

    public HomePresent(Activity activity, IHomeView homeView) {
        mHomeView = homeView;
        sExplosionView = initExplo(activity);
    }

    @Override
    public void initBle(Activity context) {
        //判断是否支持ble
        boolean supportBLE = checkSupportBLE(context);
        if (supportBLE) {
            mBluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            //俩种获取bluetoothAdapter 的方式
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (null == mBluetoothAdapter) {
                mHomeView.showToast("请打开蓝牙功能");
            } else if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled()) {
                //开启蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLE);
            }
//            else if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled()) {
//                switchTrue();
//                openBle();
//            }
        } else {
            mHomeView.showToast("设备不支持BLE功能");
        }
    }

    @Override
    public void openBle(Activity context) {
        //getBluetoothAdvertiser也是会调用下面的方法去判断是否支持ble
//                boolean isSupported = mBluetoothAdapter.isMultipleAdvertisementSupported();
        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (null == mBluetoothAdvertiser) {
            mHomeView.showToast("设备不支持BLE功能");
            switchFalse();
        } else {
            mMockServerCallBack = new MockServerCallBack();
            //打开BluetoothGattServer
            mGattServer = mBluetoothManager.openGattServer(context, mMockServerCallBack);
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

    @Override
    public void closeBle() {
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
    public void onDestroy() {
        mHomeView = null;
        closeBle();
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BLE:
                if (resultCode == RESULT_OK) {
                    mHomeView.showToast("蓝牙已启用");
                    switchTrue();
                } else {
                    mHomeView.showToast("蓝牙未启用");
                    switchFalse();
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (sIsFirstEnter) {
            sExplosionView.setMode(ExplosionView.MODE.ANNULUS);
            sExplosionView.explode(mHomeView.getIvAvatar());
            sIsFirstEnter = false;
        }
    }

    @Override
    public void changeMode(Activity context) {
        if (sExplosionView.getMode() == ExplosionView.MODE.ANNULUS) {
            openBle(context);
        } else {
            closeBle();
        }
    }

    private static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        //设置广播的模式,应该是跟功耗相关
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setConnectable(connectable);
        builder.setTimeout(timeoutMillis);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        return builder.build();
    }

    //设置一下FMP广播数据
    private static AdvertiseData createFMPAdvertiseData() {
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

    private boolean checkSupportBLE(Activity context) {
        return context.getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? true : false;
    }

    @NonNull
    private ExplosionView initExplo(Activity activity) {
        final ExplosionView explosionView = new ExplosionView(activity, new FlyawayFactory());
        explosionView.setHideStatusBar(true);
        explosionView.setSrc(R.mipmap.logo_pink);
        return explosionView;
    }

    @Override
    public void switchTrue() {
        if (null != sExplosionView && null != mHomeView.getIvAvatar()) {
            sExplosionView.setMode(ExplosionView.MODE.EXPLOSION);
            sExplosionView.explode(mHomeView.getIvAvatar());
        }
    }

    @Override
    public void switchFalse() {
        if (null != sExplosionView && null != mHomeView.getIvAvatar()) {
            sExplosionView.setMode(ExplosionView.MODE.ANNULUS);
            sExplosionView.explode(mHomeView.getIvAvatar());
        }

    }

    @Override
    public void share(Activity activity) {
        UMWeb web = new UMWeb("http://www.hypers.com/reno/");
        web.setTitle("我正在使用Reno");
        web.setThumb(new UMImage(activity, R.mipmap.logo_pink_mini));
        web.setDescription("快来下载吧");
        new ShareAction(activity).withMedia(web)
                .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                        //分享开始的回调
                    }

                    @Override
                    public void onResult(SHARE_MEDIA platform) {
                        mHomeView.showToast(platform + " 分享成功啦!");
                    }

                    @Override
                    public void onError(SHARE_MEDIA platform, Throwable t) {
                        mHomeView.showToast(platform + " 分享失败啦!");
                        if (t != null) {
                            Log.e("throw", "throw:" + t.getMessage());
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        mHomeView.showToast(platform + " 分享取消了!");
                    }
                }).open();
    }
}
