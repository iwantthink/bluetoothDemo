package com.hypers.www.bluetooth.base;

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
        PlatformConfig.setSinaWeibo("2290967974", "4a6a23f35f7eb19fcbbd212b0a77c4dd", "http://sns.whalecloud.com");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        HMTAgent.Initialize(this);
        HMTAgent.onError(this);
        UMShareAPI.get(this);
    }

}

