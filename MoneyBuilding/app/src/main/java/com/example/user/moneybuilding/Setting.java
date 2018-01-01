package com.example.user.moneybuilding;

import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.KeyEvent;

import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Setting extends AppCompatActivity {
    private RequestQueue queue1,queue2;
    private String userID;
    private ToggleButton pushButton;
    private String state = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        userID = getSharedPreferences("data", MODE_PRIVATE)
                .getString("userID", "");
        queue1 = Volley.newRequestQueue(this);
        queue2 = Volley.newRequestQueue(this);
        pushButton = this.findViewById(R.id.toggleButton);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL)+"PersonalInformationServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("1")){
                            pushButton.setChecked(true);
                            state = "1";
                        }else
                            state = "0";
                    }
                }, new Response.ErrorListener() {
            // @Override
            public void onErrorResponse(VolleyError error) {    //錯誤訊息
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("state", "getPushState");
                map.put("userID", userID);
                return map;
            }
        };
        queue1.add(stringRequest);   //把request丟進queue(佇列)

        pushButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    state = "1";
                else
                    state = "0";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL)+"PersonalInformationServlet",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        }, new Response.ErrorListener() {
                    // @Override
                    public void onErrorResponse(VolleyError error) {    //錯誤訊息
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("state", "setPushState");
                        map.put("userID", userID);
                        map.put("pushState", state);
                        return map;
                    }
                };
                queue2.add(stringRequest);   //把request丟進queue(佇列)
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();
            intent.setClass(Setting.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
