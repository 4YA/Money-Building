package com.example.user.moneybuilding;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {
    private Button registerButton;
    private Button forgotPasswordButton;
    private Button loginButton;
    private EditText accountText;
    private EditText passwordText;
    private RequestQueue queue1, queue2, queue3, queue4;
    private SignInButton googleSignInButton;
    private GoogleApiClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private String googleEmail, googleName;
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        googleSignInButton = (SignInButton)findViewById(R.id.googleSignInButton);
        setGooglePlusButtonText(googleSignInButton, "Google 快速登入");
        registerButton = (Button)findViewById(R.id.registerButton);
        forgotPasswordButton = (Button)findViewById(R.id.forgotPasswordButton);
        loginButton = (Button)findViewById(R.id.loginButton);
        accountText=(EditText) this.findViewById(R.id.accountText);
        passwordText=(EditText) this.findViewById(R.id.passwordText);
        queue1 = Volley.newRequestQueue(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
        googleSignInButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
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
                    queue1.add(stringRequest);   //把request丟進queue(佇列)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != RESULT_CANCELED){
            if(requestCode == RC_SIGN_IN && data != null){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        Log.v("sucess", "handleSignInResult: "+result.isSuccess());
        if(result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        }else{
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        googleEmail = acct.getEmail();
        googleName = acct.getDisplayName();
        queue2 = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL)+"LoginServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("狀態", response);
                        if (response.equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                            builder.setTitle("設定密碼");
                            builder.setMessage("第一次登入，請設定App密碼");
                            LinearLayout parentLayout = new LinearLayout(Login.this);
                            final EditText editText = new EditText(Login.this);
                            editText.setHint("6-12字由字母或數字組成");
                            editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);

                            // call the dimen resource having value in dp: 16dp
                            int left = getPixelValue((int)getResources().getDimension(R.dimen.activity_horizontal_margin));
                            int right = getPixelValue((int)getResources().getDimension(R.dimen.activity_horizontal_margin));

                            // this will set the margins
                            layoutParams.setMargins(left, 0, right, 0);

                            editText.setLayoutParams(layoutParams);
                            parentLayout.addView(editText);
                            builder.setView(parentLayout);
                            builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(editText.getText().length() > 5) {
                                        queue3 = Volley.newRequestQueue(Login.this);
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL) + "RegisterServlet",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Log.v("狀態", response);
                                                        if(!response.equals("")) {
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
                                                map.put("state", "newGoogleAccount");
                                                map.put("email", googleEmail);
                                                map.put("password", editText.getText().toString());
                                                map.put("name", googleName);
                                                return map;
                                            }
                                        };
                                        queue3.add(stringRequest);   //把request丟進queue(佇列)
                                    }else{
                                        new AlertDialog.Builder(Login.this)
                                                .setTitle("提示")//設定視窗標題
                                                .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                .setMessage("密碼格式錯誤")//設定顯示的文字
                                                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })//設定結束的子視窗
                                                .show();//呈現對話視窗
                                    }
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            builder.create().show();

                        }else {
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
                Map<String, String> map = new HashMap<String, String>();
                map.put("state", "googleLoginVerification");
                map.put("email", googleEmail);
                return map;
            }
        };
        queue2.add(stringRequest);
    }

    private int getPixelValue(int dp) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, resources.getDisplayMetrics());
    }
    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
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
