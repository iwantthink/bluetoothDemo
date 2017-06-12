package com.hypers.www.bluetooth.login.v;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hypers.www.bluetooth.HomeActivity;
import com.hypers.www.bluetooth.R;
import com.hypers.www.bluetooth.login.p.LoginPresentImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements ILoginView {

    @BindView(R.id.et_psw)
    EditText mEtPsw;
    @BindView(R.id.tv_register)
    TextView mTvRegister;
    @BindView(R.id.tv_find_psw)
    TextView mTvFindPsw;
    @BindView(R.id.rl_func)
    RelativeLayout mRlFunc;
    @BindView(R.id.activity_login)
    RelativeLayout mActivityLogin;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.et_account)
    EditText mEtAccount;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.iv_psw_visibility)
    ImageView mIvPswVisibility;

    private LoginPresentImpl mLoginPresent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        mLoginPresent = new LoginPresentImpl(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginPresent.onDestroy();
    }

    private void initView() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginPresent.login(getUserName(), getPassword());
            }
        });

        mEtPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    showPswVisibility(true);
                } else {
                    showPswVisibility(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void showProgressBar(boolean isShow) {
        mPb.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void clearUserName() {
        mEtAccount.setText("");
    }

    @Override
    public void clearPassword() {
        mEtPsw.setText("");
    }

    @Override
    public void toHomeActivity() {
        HomeActivity.start(LoginActivity.this);
    }

    @Override
    public void showFailedError(int code, String msg) {
        if (code == 1) {
            mEtAccount.setError(msg);
        } else if (code == 2) {
            mEtPsw.setError(msg);
        } else if (code == 3) {
            mEtPsw.setError(msg);
            mEtAccount.setError(msg);
        }
    }

    @Override
    public String getUserName() {
        return mEtAccount.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return mEtPsw.getText().toString().trim();
    }

    @Override
    public void showPswVisibility(boolean isShow) {
        if (isShow) {
            mIvPswVisibility.setImageResource(R.mipmap.open_eye);
        } else {
            mIvPswVisibility.setImageResource(R.mipmap.close_eye);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
