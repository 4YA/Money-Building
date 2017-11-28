package com.example.user.moneybuilding;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    private ImageView selectCharacter;
    private ImageView backImageView;
    private RequestQueue queue1, queue2;
    private Button sendButton;
    private int nowSelect = 0;
    private EditText email;
    private EditText password;
    private EditText checkPassword;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        selectCharacter = this.findViewById(R.id.selectCharacter);
        backImageView = this.findViewById(R.id.backImageView);
        sendButton = this.findViewById(R.id.registerButton);
        email = Register.this.findViewById(R.id.emailText);
        password = Register.this.findViewById(R.id.passwordText);
        checkPassword =  Register.this.findViewById(R.id.checkPasswordText);
        name = Register.this.findViewById(R.id.nameText);
        SharedPreferences pref = getSharedPreferences("temp", MODE_PRIVATE);
        email.setText(pref.getString("email" , ""));
        password.setText(pref.getString("password" , ""));
        checkPassword.setText(pref.getString("checkPassword" , ""));
        name.setText(pref.getString("name" , ""));
        pref.edit().clear().commit();
        Bundle bundle = this.getIntent().getExtras();
        nowSelect = bundle.getInt("nowSelect");
        if(nowSelect != 0) {
            GetXMLTask task = new GetXMLTask();
            // Execute the task
            task.execute(new String[]{"http://140.121.197.130:8004/Money-Building/CharacterServlet?state=getCharacter&characterID=" + nowSelect});
        }

        queue1 = Volley.newRequestQueue(this);
        queue2 = Volley.newRequestQueue(this);
        selectCharacter.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://140.121.197.130:8004/Money-Building/CharacterServlet?state=getImageCount",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences pref = getSharedPreferences("temp", MODE_PRIVATE);
                        pref.edit()
                                .putString("email", email.getText().toString())
                                .putString("password", password.getText().toString())
                                .putString("checkPassword", checkPassword.getText().toString())
                                .putString("name", name.getText().toString())
                                .commit();
                        Intent intent = new Intent();
                        intent.setClass(Register.this, Character.class);
                        intent.putExtra("count", Integer.valueOf(response));
                        intent.putExtra("nowSelect", nowSelect);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
            // @Override
            public void onErrorResponse(VolleyError error) {    //錯誤訊息
            }
        });
                queue1.add(stringRequest);   //把request丟進queue(佇列)
            }
        });
        sendButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String passwordString = String.format(password.getText().toString());
                String checkPasswordString = String.format(checkPassword.getText().toString());
                int passwordNum = passwordString.length();
                String nameString = String.format(name.getText().toString());
                if(Linkify.addLinks(email.getText(), Linkify.EMAIL_ADDRESSES)){
                    if(passwordNum > 5){
                        if(passwordString.equals(checkPasswordString)) {
                            if (nameString.length() != 0) {
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://140.121.197.130:8004/Money-Building/RegisterServlet",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if(response.equals("1")){
                                                    new AlertDialog.Builder(Register.this)
                                                            .setTitle("提示")//設定視窗標題
                                                            .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                            .setMessage("註冊成功")//設定顯示的文字
                                                            .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent();
                                                                    intent.setClass(Register.this, Login.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })//設定結束的子視窗
                                                            .show();//呈現對話視窗
                                                }else{
                                                    new AlertDialog.Builder(Register.this)
                                                            .setTitle("提示")//設定視窗標題
                                                            .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                                            .setMessage("電子信箱已被使用")//設定顯示的文字
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
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("state", "newAccount");
                                        map.put("email", String.format(email.getText().toString()));
                                        map.put("password", String.format(password.getText().toString()));
                                        map.put("name", String.format(name.getText().toString()).replace("\'", "&#39;").replace("\"", "&#34;").replace("\\", "&#92;"));
                                        map.put("icon", nowSelect+"");
                                        return map;
                                    }
                                };
                                queue2.add(stringRequest);   //把request丟進queue(佇列)
                            } else {
                                new AlertDialog.Builder(Register.this)
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
                            new AlertDialog.Builder(Register.this)
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
                        new AlertDialog.Builder(Register.this)
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
                }else{
                    new AlertDialog.Builder(Register.this)
                            .setTitle("提示")//設定視窗標題
                            .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                            .setMessage("Email格式錯誤")//設定顯示的文字
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
    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            selectCharacter.setImageBitmap(result);
            backImageView.setVisibility(View.VISIBLE);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }
}
