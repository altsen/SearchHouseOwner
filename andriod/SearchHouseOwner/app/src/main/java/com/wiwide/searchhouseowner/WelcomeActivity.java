package com.wiwide.searchhouseowner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.wiwide.common.CommonUtil;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        int mainColor = getResources().getColor(R.color.welcomeColor);
        CommonUtil.setStatusBarColor(this, mainColor);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        }, 1500);
    }
}
