package com.hypers.www.bluetooth;

import android.app.Application;

import com.hmt.analytics.HMTAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by renbo on 2017/5/23.
 */

public class BaseApplication extends Application {
    {
        PlatformConfig.setWeixin("wxf83c996d3fa9f5f1", "59c9ac2f053a3b73585ca6b417d9a12a");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        HMTAgent.Initialize(this);
        UMShareAPI.get(this);
    }
}
