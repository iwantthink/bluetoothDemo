package com.hypers.www.bluetooth.base;

import com.hypers.www.bluetooth.home.component.HomeComponent;

public class ComponentHolder {
    private static HomeComponent sHomeComponent;

    public static HomeComponent getHomeComponent() {
        return sHomeComponent;
    }

    public static void setHomeComponent(HomeComponent homeComponent) {
        sHomeComponent = homeComponent;
    }
}
