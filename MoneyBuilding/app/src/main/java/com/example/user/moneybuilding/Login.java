package com.example.user.moneybuilding;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {
    private Button registerButton;
    private Button forgotPasswordButton;
    private Button loginButton;
    private EditText accountText;
    private EditText passwordText;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerButton = (Button)findViewById(R.id.registerButton);
        forgotPasswordButton = (Button)findViewById(R.id.forgotPasswordButton);
        loginButton = (Button)findViewById(R.id.loginButton);
        accountText=(EditText) this.findViewById(R.id.accountText);
        passwordText=(EditText) this.findViewById(R.id.passwordText);
        queue = Volley.newRequestQueue(this);
        registerButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Login.this, Register.class);
                intent.putExtra("newSelect", 0);
                startActivity(intent);
            }
        });
        forgotPasswordButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(Linkify.addLinks(accountText.getText(), Linkify.EMAIL_ADDRESSES)) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL)+"LoginServlet",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.v("狀態", response);
                                    if (response.equals("")) {
                                        new AlertDialog.Builder(Login.this)
                                                .setTitle("提示")//設定視窗標題
                                                .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                .setMessage("帳號或密碼輸入錯誤")//設定顯示的文字
                                                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })//設定結束的子視窗
                                                .show();//呈現對話視窗
                                    } else {
                                        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                                        pref.edit()
                                                .putString("userID", response)
                                                .commit();
                                        Intent intent = new Intent();
                                        intent.setClass(Login.this, HomePage.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        // @Override
                        public void onErrorResponse(VolleyError error) {    //錯誤訊息
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            String account = String.format(accountText.getText().toString());
                            String password = String.format(passwordText.getText().toString());
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("state", "loginVerification");
                            map.put("email", account);
                            map.put("password", password);
                            return map;
                        }
                    };
                    queue.add(stringRequest);   //把request丟進queue(佇列)
                }else{
                    new AlertDialog.Builder(Login.this)
                            .setTitle("提示")//設定視窗標題
                            .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                            .setMessage("帳號或密碼輸入錯誤")//設定顯示的文字
                            .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })//設定結束的子視窗
                            .show();//呈現對話視窗
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(Login.this)
                    .setTitle("離開")
                    .setMessage("確定關閉Money-Building")
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return true;
    }
}
