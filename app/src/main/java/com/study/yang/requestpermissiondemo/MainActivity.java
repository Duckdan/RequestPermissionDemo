package com.study.yang.requestpermissiondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvPermission = (TextView) findViewById(R.id.tv_permission);
        String permission = getIntent().getStringExtra("permission");
        tvPermission.setText(permission);
    }
}
