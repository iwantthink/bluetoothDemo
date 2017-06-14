package com.hypers.www.bluetooth.home.module;

import android.app.Activity;

import com.hypers.www.bluetooth.home.p.HomePresent;
import com.hypers.www.bluetooth.home.v.IHomeView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by renbo on 2017/6/14.
 */
@Module
public class HomeModule {

    private final Activity mActivity;
//    private IHomeView mHomeView;

    public HomeModule(Activity activity) {
        mActivity = activity;
//        mHomeView = homeView;
    }

    @Provides
    public IHomeView provideIHomeView() {
        return (IHomeView) mActivity;
    }

    @Provides
    public Activity provideActivity() {
        return mActivity;
    }

    @Provides
    public HomePresent provideHomePresent(IHomeView iHomeView, Activity activity) {
        return new HomePresent(activity, iHomeView);
    }

}
