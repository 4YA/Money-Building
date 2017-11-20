package com.example.user.moneybuilding;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class Welcome extends AppCompatActivity {
    private Button btn;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);
        mHandler.sendEmptyMessageDelayed(0, 2000);//2秒跳轉

    }
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
            Log.v("是否登入",pref.getString("userID" , "0"));
            Intent intent = new Intent();
            if(pref.getString("userID" , "0").equals("0"))
                intent.setClass(Welcome.this, Login.class);
            else
                intent.setClass(Welcome.this, HomePage.class);
            startActivity(intent);
            finish();
        }
    };
}
