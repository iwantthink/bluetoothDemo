package com.hypers.www.bluetooth.login.v;

/**
 * Created by renbo on 2017/5/2.
 */

public interface ILoginView {

    void showProgressBar(boolean isShow);

    void clearUserName();

    void clearPassword();

    void toHomeActivity();

    void showFailedError();

    String getUserName();

    String getPassword();
}
