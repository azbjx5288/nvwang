package com.neinei.cong.ui;

import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.neinei.cong.R;
import com.neinei.cong.base.BaseActivity;
import com.neinei.cong.ui.login.ModifyPwdActivity;
import com.neinei.cong.utils.DialogHelp;
import com.neinei.cong.utils.LoginUtils;
import com.neinei.cong.utils.UpdateManager;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private com.neinei.cong.widget.LineControlView mResetSetting;
    private com.neinei.cong.widget.LineControlView mChecklastetSetting;
    private android.widget.Button mBtnExitLogin;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        mResetSetting = findViewById(R.id.reset_setting);
        mChecklastetSetting = findViewById(R.id.checklastet_setting);
        mBtnExitLogin = findViewById(R.id.btn_exitLogin);
        setToolbar("设置", true);
    }

    @Override
    protected void setListener() {
        mResetSetting.setOnClickListener(this);
        mChecklastetSetting.setOnClickListener(this);
        mBtnExitLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_setting:
                ActivityUtils.startActivity(ModifyPwdActivity.class);
                break;
            case R.id.checklastet_setting:
                checkOut();
                break;
            case R.id.btn_exitLogin:
                DialogHelp.getConfirmDialog(this, "确定要退出登录吗？", (dialog, which) -> LoginUtils.ExitLoginStatus()).show();
                break;
            default:
                break;
        }
    }

    private void checkOut() {
        new UpdateManager(this, true).checkUpdate();
    }
}
