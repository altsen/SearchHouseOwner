package com.wiwide.searchhouseowner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主界面
 * Created by yueguang on 16-1-25.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private EditText mPhone;
    private Button mCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhone = (EditText) findViewById(R.id.phone);
        mCheck = (Button) findViewById(R.id.check);
        mCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (isPhoneOk(mPhone.getText().toString())) {
            Intent result = new Intent(this, ResultActivity.class);
            startActivity(result);
        } else {
            Toast.makeText(this, R.string.phone_err, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPhoneOk(String phone) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }
}
