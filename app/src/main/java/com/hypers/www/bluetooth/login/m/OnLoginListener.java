package com.hypers.www.bluetooth.login.m;

/**
 * Created by renbo on 2017/5/2.
 */

public interface OnLoginListener {
    void loginSuccess(User user);

    void loginFail(int code, String msg);
}
