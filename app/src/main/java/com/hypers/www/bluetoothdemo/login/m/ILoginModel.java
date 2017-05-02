package com.hypers.www.bluetoothdemo.login.m;

/**
 * Created by renbo on 2017/5/2.
 */

public interface ILoginModel {
    /**
     * 登录操作
     *
     * @param account
     * @param password
     */
    void login(String account, String password ,OnLoginListener listener);
}
