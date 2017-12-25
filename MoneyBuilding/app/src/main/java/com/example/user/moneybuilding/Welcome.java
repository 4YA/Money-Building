package com.example.user.moneybuilding;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;


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
            else {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null && bundle.get("title")!=null) {
                    switch (bundle.get("tag").toString()){
                        case "addMember":
                            intent.setClass(Welcome.this, MainTallyBook.class);
                            break;
                        case "goalAchieved":
                            intent.setClass(Welcome.this, MainTallyBook.class);
                            break;
                        default:
                            Log.v("switch","default");
                            intent.setClass(Welcome.this, HomePage.class);
                    }
                } else {
                    intent.setClass(Welcome.this, HomePage.class);
                }
            }
            startActivity(intent);
            finish();
        }
    };
}
