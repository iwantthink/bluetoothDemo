package com.hypers.www.bluetooth.home.v;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.hypers.www.bluetooth.R;
import com.hypers.www.bluetooth.home.component.DaggerHomeComponent;
import com.hypers.www.bluetooth.home.component.HomeComponent;
import com.hypers.www.bluetooth.home.module.HomeModule;
import com.hypers.www.bluetooth.home.p.IHomePresent;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HomeActivity extends AppCompatActivity implements IHomeView {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private ImageView mIvAvatar;
    private ImageView mIvShare;
    private ImageView mIvAbout;
    public IHomePresent mHomePresent;

    public static void start(Activity context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        initView();
        HomeComponent homeComponent = DaggerHomeComponent.
                builder().
                homeModule(new HomeModule(this)).
                build();
        mHomePresent = homeComponent.getHomePresent();
        mHomePresent.initBle();
        initListener();
    }

    private void initListener() {
        RxView.clicks(mIvAvatar).
                throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        mHomePresent.changeMode();
                    }
                });

        RxView.clicks(mIvShare).
                throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        mHomePresent.share();
                    }
                });

        RxView.clicks(mIvAbout).
                throttleFirst(1, TimeUnit.SECONDS).
                subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        showAbout();
                    }
                });

    }

    private void initView() {
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mIvAbout = (ImageView) findViewById(R.id.iv_about);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mHomePresent.onWindowFocusChanged(hasFocus);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHomePresent.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomePresent.onDestroy();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ImageView getIvAvatar() {
        return mIvAvatar;
    }

    @Override
    public void showAbout() {
        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this).
                setTitle("关于").
                setMessage("REON是一款具有交互功能的运动类景观灯光装置。在慢跑运动风靡的今天，将多媒体灯光、APP与跑步相结合，打造出独具魅力的互动灯光跑道。").
                create();
        dialog.show();
    }

}
