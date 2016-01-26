package com.wiwide.searchhouseowner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 房源信息的展示界面
 * Created by yueguang on 16-1-25.
 */
public class HouseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_house);


        initData(getIntent());
    }

    private void initData(Intent intent) {
        try {
            String result = intent.getStringExtra(MainActivity.RESULT);
            JSONObject info = new JSONObject(result);
            SimpleDraweeView image = (SimpleDraweeView) findViewById(R.id.image);
            image.setImageURI(Uri.parse(info.optString("img")));

            TextView location = (TextView) findViewById(R.id.location);
            location.setText(info.optString("weizhi"));

            TextView court = (TextView) findViewById(R.id.court);
            court.setText(info.optString("xiaoqu"));

            TextView price = (TextView) findViewById(R.id.price);
            price.setText(info.optString("price"));

            TextView owner = (TextView) findViewById(R.id.owner_tip);
            owner.setText(info.optString("owner"));

            StringBuilder detailsInfo = new StringBuilder();
            detailsInfo.append(info.optString("peizhi"));
            detailsInfo.append(info.optString("gaikuang"));
            detailsInfo.append(info.optString("louceng"));
            detailsInfo.append(info.optString("huxing"));

            TextView details = (TextView) findViewById(R.id.details);
            details.setText(detailsInfo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
