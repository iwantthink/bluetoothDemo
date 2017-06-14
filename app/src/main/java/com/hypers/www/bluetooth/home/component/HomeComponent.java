package com.hypers.www.bluetooth.home.component;

import com.hypers.www.bluetooth.home.module.HomeModule;
import com.hypers.www.bluetooth.home.p.HomePresent;

import dagger.Component;

/**
 * Created by renbo on 2017/6/14.
 */
@Component(modules = {HomeModule.class})
public interface HomeComponent {
    HomePresent getHomePresent();
}
