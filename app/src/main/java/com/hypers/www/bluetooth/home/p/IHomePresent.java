package com.hypers.www.bluetooth.home.p;

import android.content.Intent;

/**
 * Created by renbo on 2017/6/12.
 */

public interface IHomePresent {

    void initBle();

    void openBle();

    void closeBle();

    void onDestroy();

    void onWindowFocusChanged(boolean hasFocus);

    void changeMode();

    void switchTrue();

    void switchFalse();

    void share();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
