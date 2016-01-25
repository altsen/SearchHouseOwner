package com.wiwide.searchhouseowner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.apache.http.Header;

/**
 * 检查是否为真房东的结果界面
 * Created by yueguang on 16-1-25.
 */
public class ResultActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView mResult;
    private ListView mHouseList;
    private Button mCall;
    private HouseAdapter mHouseAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_result);

        mResult = (TextView) findViewById(R.id.result);
        mHouseList = (ListView) findViewById(R.id.house_list);
        mHouseAdpter = new HouseAdapter();
        mHouseList.setAdapter(mHouseAdpter);
        mHouseList.setOnItemClickListener(this);
        mCall = (Button) findViewById(R.id.call);
        mCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

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
                Toast.makeText(ResultActivity.this, R.string.net_err, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Intent result = new Intent(ResultActivity.this, HouseActivity.class);
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
            return 2;
        }

        @Override
        public Object getItem(int i) {
            return null;
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

            return convertView;
        }
    }

    class ViewHolder {
        public SimpleDraweeView mImage;
        public TextView mHouse;
        public String mId;

        public ViewHolder(View itemView) {
            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
            mHouse = (TextView) itemView.findViewById(R.id.house);
        }
    }

}
