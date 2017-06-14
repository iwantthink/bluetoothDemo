package com.hypers.www.bluetooth.home.module;

import android.app.Activity;

import com.hypers.www.bluetooth.home.p.HomePresent;

import dagger.Module;
import dagger.Provides;

/**
 * Created by renbo on 2017/6/14.
 */
@Module
public class HomeModule {

    private final Activity mActivity;

    public HomeModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    public Activity provideActivity() {
        return mActivity;
    }

    @Provides
    public HomePresent provideHomePresent(Activity activity) {
        return new HomePresent(activity);
    }

}
