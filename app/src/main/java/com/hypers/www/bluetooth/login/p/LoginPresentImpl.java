package com.hypers.www.bluetooth.login.p;

import com.hypers.www.bluetooth.login.m.ILoginModel;
import com.hypers.www.bluetooth.login.m.LoginModelImpl;
import com.hypers.www.bluetooth.login.m.OnLoginListener;
import com.hypers.www.bluetooth.login.m.User;
import com.hypers.www.bluetooth.login.v.ILoginView;

/**
 * Created by renbo on 2017/5/2.
 */

public class LoginPresentImpl implements ILoginPresent, OnLoginListener {

    private ILoginView mLoginView;
    private ILoginModel mLoginModel;

    public LoginPresentImpl(ILoginView view) {
        mLoginView = view;
        mLoginModel = new LoginModelImpl();
    }

    @Override
    public void login(String account, String password) {
        mLoginView.showProgressBar(true);
        mLoginModel.login(account, password, this);
    }

    @Override
    public void onDestroy() {
        mLoginView = null;
    }

    @Override
    public void loginSuccess(User user) {
        if (null != mLoginView) {
            mLoginView.showProgressBar(false);
            mLoginView.toHomeActivity();
        }
    }

    @Override
    public void loginFail(int code, String msg) {
        if (null != mLoginView) {
            mLoginView.showProgressBar(false);
            mLoginView.showFailedError(code, msg);
        }
    }
}
