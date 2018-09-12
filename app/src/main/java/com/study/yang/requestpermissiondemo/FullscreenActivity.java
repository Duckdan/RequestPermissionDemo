package com.study.yang.requestpermissiondemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class FullscreenActivity extends AppCompatActivity {


    private String[] rps = {Manifest.permission.READ_PHONE_STATE};
    private static final int REQUEST_PERMISSION_SETTING = 200;
    private AlertDialog permissionDialog;
    private TextView tvOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && permissions.length > 0) {
            boolean flag = true;
            for (int i = 0; i < permissions.length; i++) {
                flag &= (grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
            //如果权限赋予失败
            if (!flag) {
                //如果勾选且拒绝了权限申请，则返回false
                boolean permissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                showPermissionDialog(!permissionRationale);
            } else {
                requestPermissionSuccess();
            }
        }
    }

    private void showPermissionDialog(final boolean isCheck) {
        if (permissionDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog);
            View inflate = LayoutInflater.from(this).inflate(R.layout.permission_dialog_layout, null);
            permissionDialog = builder.create();
            permissionDialog.show();
            permissionDialog.setCancelable(false);

            Window window = permissionDialog.getWindow();
            //设置遮罩的亮度
//        window.setDimAmount(0);
           window.setContentView(inflate);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            tvOk = (TextView) inflate.findViewById(R.id.tv_ok);
        } else {
            permissionDialog.show();
        }

        Resources resources = getResources();
        tvOk.setText(isCheck ? resources.getString(R.string.permission_open) : resources.getString(R.string.permission_allow));
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheck) {
                    openSettingActivity();
                } else {
                    PermissionCheckUtils.checkActivityPermissions(FullscreenActivity.this, rps, 100, null);
                }
                permissionDialog.dismiss();
            }
        });
    }

    /**
     * 关掉对话框
     */
    private void dismissPermissionDialog() {
        if (permissionDialog != null && permissionDialog.isShowing()) {
            permissionDialog.dismiss();
        }
    }

    /**
     * 打开应用详情页
     */
    private void openSettingActivity() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        Uri uri = Uri.fromParts("package", getPackageName(), null);

        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    /**
     * 用来处理从应用详情页返回之后的接受结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            //返回当前页的时候再次校验权限
            int size = PermissionCheckUtils.checkActivityPermissions(this, rps, 100, null);
            //未被授予则进行再次申请
            if (size > 0) {
                showPermissionDialog(!ActivityCompat.shouldShowRequestPermissionRationale(this, rps[0]));
            } else {
                //请求权限成功
                requestPermissionSuccess();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        int permissionSize = PermissionCheckUtils.checkActivityPermissions(this, rps, 100, null);
        if (permissionSize == 0) {
            requestPermissionSuccess();
        }
    }

    private void requestPermissionSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("permission", "赋予权限成功");
        startActivity(intent);
    }
}
