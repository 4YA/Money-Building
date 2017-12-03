package com.example.user.moneybuilding;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {
    private EditText emailText;
    private Button getPasswordButton;
    private RequestQueue queue1, queue2;
    private LinearLayout newLayout;
    private int textState = 0;
    private String tempEmail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailText = this.findViewById(R.id.emailText);
        getPasswordButton = this.findViewById(R.id.getPasswordButton);
        newLayout = this.findViewById(R.id.newLayout);
        queue1 = Volley.newRequestQueue(this);
        queue2 = Volley.newRequestQueue(this);
        getPasswordButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(textState == 0) {
                    if(Linkify.addLinks(emailText.getText(), Linkify.EMAIL_ADDRESSES)) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL)+"ForgotPasswordServlet",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.equals("1")) {
                                            new AlertDialog.Builder(ForgotPassword.this)
                                                    .setTitle("提示")//設定視窗標題
                                                    .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                    .setMessage("已發送驗證碼至" + emailText.getText())//設定顯示的文字
                                                    .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            textState = 1;
                                                            TextView vcString = new TextView(ForgotPassword.this);
                                                            EditText vcText = new EditText(ForgotPassword.this);
                                                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                            params1.setMargins(DPtoPX(20), DPtoPX(17), DPtoPX(5), DPtoPX(10));
                                                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                                                            params2.setMargins(0, DPtoPX(15), DPtoPX(15), DPtoPX(10));
                                                            vcString.setLayoutParams(params1);
                                                            vcString.setTextSize(22);
                                                            vcString.setText("驗證");
                                                            vcString.setId(R.id.vcString);
                                                            vcText.setLayoutParams(params2);
                                                            vcText.setTextSize(20);
                                                            vcText.setHint("請輸入收到驗證碼");
                                                            vcText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                                            vcText.setTextColor(Color.BLACK);
                                                            vcText.setId(R.id.vcText);
                                                            vcText.setFilters(new InputFilter[]{
                                                                    new InputFilter.LengthFilter(10)
                                                            });
                                                            emailText.setFocusable(false);
                                                            emailText.setFocusableInTouchMode(false);
                                                            getPasswordButton.setText("取回密碼");
                                                            newLayout.addView(vcString);
                                                            newLayout.addView(vcText);
                                                        }
                                                    })//設定結束的子視窗
                                                    .show();//呈現對話視窗
                                        } else {
                                            new AlertDialog.Builder(ForgotPassword.this)
                                                    .setTitle("提示")//設定視窗標題
                                                    .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                    .setMessage("電子信箱輸入錯誤")//設定顯示的文字
                                                    .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })//設定結束的子視窗
                                                    .show();//呈現對話視窗
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            // @Override
                            public void onErrorResponse(VolleyError error) {    //錯誤訊息
                                Log.v("email","傳送失敗");
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                String email = String.format(emailText.getText().toString());
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("state", "sendVerificationCode");
                                map.put("email", email);
                                return map;
                            }
                        };
                        queue1.add(stringRequest);   //把request丟進queue(佇列)
                    }else{
                        new AlertDialog.Builder(ForgotPassword.this)
                                .setTitle("提示")//設定視窗標題
                                .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                .setMessage("電子信箱輸入錯誤")//設定顯示的文字
                                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })//設定結束的子視窗
                                .show();//呈現對話視窗
                    }
                }else{
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL)+"ForgotPasswordServlet",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.equals("")){
                                        new AlertDialog.Builder(ForgotPassword.this)
                                                .setTitle("提示")//設定視窗標題
                                                .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                .setMessage("驗證碼輸入錯誤")//設定顯示的文字
                                                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })//設定結束的子視窗
                                                .show();//呈現對話視窗
                                    }else{
                                        new AlertDialog.Builder(ForgotPassword.this)
                                                .setTitle("提示")//設定視窗標題
                                                .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                .setMessage("驗證成功，您的密碼為【"+response+"】")//設定顯示的文字
                                                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent();
                                                        intent.setClass(ForgotPassword.this, Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })//設定結束的子視窗
                                                .show();//呈現對話視窗
                                    }
                                }
                            }, new Response.ErrorListener() {
                        // @Override
                        public void onErrorResponse(VolleyError error) {    //錯誤訊息
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            String email = String.format(emailText.getText().toString());
                            EditText text = ForgotPassword.this.findViewById(R.id.vcText);
                            String verificationCode =  String.format(text.getText().toString());
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("state", "checkVerificationCode");
                            map.put("email", email);
                            map.put("verificationCode", verificationCode);
                            return map;
                        }
                    };
                    queue2.add(stringRequest);   //把request丟進queue(佇列)
                }
            }
        });

    }
    public int DPtoPX(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
