package com.example.user.moneybuilding;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class PersonalInformation extends AppCompatActivity {
    private ImageView selectCharacter;
    private ImageView backImageView;
    private RequestQueue queue1, queue2, queue3;
    private Button EditButton;
    private int nowSelect;
    private EditText email;
    private EditText password;
    private EditText checkPassword;
    private EditText name;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        userID = getSharedPreferences("data", MODE_PRIVATE)
                .getString("userID", "");
        selectCharacter = this.findViewById(R.id.selectCharacter);
        backImageView = this.findViewById(R.id.backImageView);
        EditButton = this.findViewById(R.id.EditButton);
        email = this.findViewById(R.id.emailText);
        email.setFocusable(false);
        email.setFocusableInTouchMode(false);
        password = this.findViewById(R.id.passwordText);
        checkPassword = this.findViewById(R.id.checkPasswordText);
        name = this.findViewById(R.id.nameText);
        SharedPreferences pref = getSharedPreferences("temp", MODE_PRIVATE);

        if(pref.getInt("change", 0) == 1) {
            email.setText(pref.getString("email", ""));
            password.setText(pref.getString("password", ""));
            name.setText(pref.getString("name", ""));
            checkPassword.setText(pref.getString("checkPassword" , ""));
            pref.edit().clear().commit();
            Bundle bundle = this.getIntent().getExtras();
            nowSelect = bundle.getInt("nowSelect");
            Picasso.with(PersonalInformation.this).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=" + nowSelect).transform(new CircleTransform())
                    .into(selectCharacter);
            backImageView.setVisibility(View.VISIBLE);
        }
        else {
            queue1 = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL) + "PersonalInformationServlet",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray arr = new JSONArray(response);
                                name.setText(arr.get(1).toString());
                                //取得名字&角色圖片
                                Picasso.with(PersonalInformation.this).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=" + arr.get(0).toString()).transform(new CircleTransform())
                                        .into(selectCharacter);
                                backImageView.setVisibility(View.VISIBLE);
                                email.setText(arr.get(2).toString());
                                password.setText(arr.get(3).toString());
                                checkPassword.setText(arr.get(3).toString());
                            } catch (Throwable t) {
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
                    map.put("state", "getUserAllData");
                    map.put("userID", userID);
                    return map;
                }
            };
            queue1.add(stringRequest);   //把request丟進queue(佇列)
        }

        queue2 = Volley.newRequestQueue(this);
        selectCharacter.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.servletURL)+"CharacterServlet?state=getImageCount",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {;
                                SharedPreferences pref = getSharedPreferences("temp", MODE_PRIVATE);
                                pref.edit()
                                        .putInt("change", 1)
                                        .putString("email", email.getText().toString())
                                        .putString("password", password.getText().toString())
                                        .putString("checkPassword", checkPassword.getText().toString())
                                        .putString("name", name.getText().toString())
                                        .commit();
                                Intent intent = new Intent();
                                intent.setClass(PersonalInformation.this, Character.class);
                                intent.putExtra("count", Integer.valueOf(response));
                                intent.putExtra("nowSelect", nowSelect);
                                intent.putExtra("page", "PersonalInformation");
                                startActivity(intent);
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    // @Override
                    public void onErrorResponse(VolleyError error) {    //錯誤訊息
                    }
                });
                queue2.add(stringRequest);   //把request丟進queue(佇列)
            }
        });

        queue3= Volley.newRequestQueue(this);
        EditButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String passwordString = String.format(password.getText().toString());
                String checkPasswordString = String.format(checkPassword.getText().toString());
                int passwordNum = passwordString.length();
                String nameString = String.format(name.getText().toString());
                if(passwordNum > 5) {
                    if(passwordString.equals(checkPasswordString)) {
                        if (nameString.length() != 0) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.servletURL) + "PersonalInformationServlet",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if (response.equals("1")) {
                                                new AlertDialog.Builder(PersonalInformation.this)
                                                        .setTitle("提示")//設定視窗標題
                                                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                        .setMessage("修改成功")//設定顯示的文字
                                                        .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })//設定結束的子視窗
                                                        .show();//呈現對話視窗
                                            } else {
                                                Log.v("PersonalInformation", "修改失敗");
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
                                    map.put("state", "editAccount");
                                    map.put("userID", userID);
                                    map.put("email", String.format(email.getText().toString()));
                                    map.put("password", String.format(password.getText().toString()));
                                    map.put("name", String.format(name.getText().toString()).replace("\'", "&#39;").replace("\"", "&#34;").replace("\\", "&#92;"));
                                    map.put("icon", nowSelect + "");
                                    return map;
                                }
                            };
                            queue3.add(stringRequest);   //把request丟進queue(佇列)
                        }else{
                            new AlertDialog.Builder(PersonalInformation.this)
                                    .setTitle("提示")//設定視窗標題
                                    .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                    .setMessage("姓名格式錯誤")//設定顯示的文字
                                    .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })//設定結束的子視窗
                                    .show();//呈現對話視窗
                        }
                    }else{
                        new AlertDialog.Builder(PersonalInformation.this)
                            .setTitle("提示")//設定視窗標題
                            .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                            .setMessage("確認密碼需與密碼相同")//設定顯示的文字
                            .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })//設定結束的子視窗
                            .show();//呈現對話視窗
                    }
                }else{
                    new AlertDialog.Builder(PersonalInformation.this)
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
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();
            intent.setClass(PersonalInformation.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
