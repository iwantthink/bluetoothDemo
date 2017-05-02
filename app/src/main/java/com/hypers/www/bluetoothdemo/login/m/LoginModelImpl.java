package com.hypers.www.bluetoothdemo.login.m;

import android.os.SystemClock;

/**
 * Created by renbo on 2017/5/2.
 */

public class LoginModelImpl implements ILoginModel {


    @Override
    public void login(final String account, final String password, final OnLoginListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(10000);
                if (account.equals("1") && password.equals("1")) {
                    User user = new User();
                    user.setUserName("hey");
                    user.setPassWord("psw");
                    listener.loginSuccess(user);
                } else {
                    listener.loginFail();
                }
            }
        }).start();
    }
}
