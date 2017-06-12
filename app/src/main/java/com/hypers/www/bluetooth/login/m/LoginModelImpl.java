package com.hypers.www.bluetooth.login.m;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

/**
 * Created by renbo on 2017/5/2.
 */

public class LoginModelImpl implements ILoginModel {

    private static final int LOGIN_SUCCESS = 111;
    private static final int LOGIN_FAILE = 222;
    private OnLoginListener mOnLoginListener;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    User user = new User();
                    user.setUserName("1");
                    user.setPassWord("1");
                    mOnLoginListener.loginSuccess(user);
                    break;
                case LOGIN_FAILE:
                    mOnLoginListener.loginFail(3, "the account or psw is error");
                    break;
            }
        }
    };

    @Override
    public void login(final String account, final String password, final OnLoginListener listener) {
        mOnLoginListener = listener;
        if (TextUtils.isEmpty(account)) {
            listener.loginFail(1, "account cant be null");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.loginFail(2, "psw cant be null");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(10000);
                if (account.equals("1") && password.equals("1")) {
                    mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(LOGIN_FAILE);
                }
            }
        }).start();
    }
}
