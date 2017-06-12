package com.hypers.www.bluetooth.home.p;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by renbo on 2017/6/12.
 */

public interface IHomePresent {

    void initBle(Activity context);

    void openBle(Activity context);

    void closeBle();

    void onDestroy();

    void onActivityResult(Context context, int requestCode, int resultCode, Intent data);

    void onWindowFocusChanged(boolean hasFocus);

    void changeMode(Activity context);

    void switchTrue();

    void switchFalse();

    void share(Activity activity);
}
