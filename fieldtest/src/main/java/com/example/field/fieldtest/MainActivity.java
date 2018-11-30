package com.example.field.fieldtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Android on 2018/10/19.
 */

public class MainActivity extends AppCompatActivity{


    private TextView agentVersionTv,douYuVersionTv,weChatVersionTv,webVersionTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();



        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }

        setTv();


    }



    private void initView() {
        agentVersionTv=(TextView) findViewById(R.id.agentVersionId);
        douYuVersionTv=(TextView) findViewById(R.id.douYuVersionId);
        webVersionTv=(TextView) findViewById(R.id.webVersionId);
        weChatVersionTv=(TextView) findViewById(R.id.weChatVersionId);
    }

    private void setTv() {
        agentVersionTv.setText(BuildConfig.VERSION_NAME);
        douYuVersionTv.setText(ConfigTest.DOUYUVERSION);
        webVersionTv.setText(ConfigTest.WEBVERSION);
        weChatVersionTv.setText(ConfigTest.WECHATVERSION);

    }

}
