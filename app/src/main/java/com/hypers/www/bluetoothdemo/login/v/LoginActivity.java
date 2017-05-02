package com.hypers.www.bluetoothdemo.login.v;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hypers.www.bluetoothdemo.R;

public class LoginActivity extends AppCompatActivity implements ILoginView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {

    }

    @Override
    public void showProgressBar(boolean isShow) {

    }

    @Override
    public void clearUserName() {

    }

    @Override
    public void clearPassword() {

    }

    @Override
    public void toHomeActivity() {

    }

    @Override
    public void showFailedError() {

    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }


}
