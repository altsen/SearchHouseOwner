package com.wiwide.searchhouseowner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wiwide.common.CommonDefine;
import com.wiwide.common.CommonUtil;
import com.wiwide.data.HouseInfo;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查是否为真房东的结果界面
 * Created by yueguang on 16-1-25.
 */
public class ResultActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView mHouseList;
    private String mPhone;
    private boolean mIsOwner;
    private List<HouseInfo> mHouseInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_result);

        int mainColor = getResources().getColor(R.color.mainColor);
        CommonUtil.setStatusBarColor(this, mainColor);

        initData(getIntent());
        mPhone = getIntent().getStringExtra(MainActivity.PHONE);
        TextView result = (TextView) findViewById(R.id.result);
        Button call = (Button) findViewById(R.id.call);
        call.setOnClickListener(this);
        if (mIsOwner) {
            result.setText(R.string.really_owner);
            call.setText(R.string.call_owner);
        } else {
            result.setText(R.string.really_agent);
            call.setText(R.string.call_agent);
        }
        mHouseList = (ListView) findViewById(R.id.house_list);
        HouseAdapter houseAdapter = new HouseAdapter();
        mHouseList.setAdapter(houseAdapter);
        mHouseList.setOnItemClickListener(this);
    }

    private void initData(Intent intent) {
        try {
            String result = intent.getStringExtra(MainActivity.RESULT);
            mPhone = intent.getStringExtra(MainActivity.PHONE);

            JSONObject info = new JSONObject(result);
            mIsOwner = !info.optString("agent").equals("yes");

            JSONArray houseInfo = info.optJSONArray("data");
            mHouseInfoList = new ArrayList<>();
            for (int i = 0; i < houseInfo.length(); i++) {
                mHouseInfoList.add(new HouseInfo(houseInfo.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + mPhone));
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams rp = new RequestParams();
        rp.add("id", holder.mId);
        asyncHttpClient.get(CommonDefine.SERVER + CommonDefine.HOUSE_INFO, rp, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("xxxxx", "statusCode:" + i + "throwable:" + throwable);
                Toast.makeText(ResultActivity.this, R.string.net_err, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i("xxxxx", "s:" + s);
                Intent result = new Intent(ResultActivity.this, HouseActivity.class);
                result.putExtra(MainActivity.RESULT, s);
                startActivity(result);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

        });
    }

    class HouseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mHouseInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return mHouseInfoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.house_item, mHouseList, false);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            HouseInfo houseInfo = mHouseInfoList.get(i);
            holder.mHouse.setText(houseInfo.mLocation);
            Uri uri = Uri.parse(houseInfo.mImage);
            holder.mImage.setImageURI(uri);
            holder.mId = houseInfo.mId;
            String price = "¥ " + houseInfo.mPrice;
            holder.mPrice.setText(price);
            return convertView;
        }
    }

    class ViewHolder {
        public SimpleDraweeView mImage;
        public TextView mHouse;
        public TextView mPrice;
        public String mId;

        public ViewHolder(View itemView) {
            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mHouse = (TextView) itemView.findViewById(R.id.house);
            mPrice = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
