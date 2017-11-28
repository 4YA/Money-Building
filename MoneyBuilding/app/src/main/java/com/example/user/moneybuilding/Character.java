package com.example.user.moneybuilding;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.widget.*;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class Character extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout characterLayout;
    private LinearLayout linearLayout;
    private int total;
    private int count = 0;
    private int nowSelect;
    String tempEmail, tempPassword, tempCheckPassword, tempName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        characterLayout = (LinearLayout) this.findViewById(R.id.linearLayout);
        Bundle bundle = this.getIntent().getExtras();
        total = bundle.getInt("count");
        nowSelect = bundle.getInt("nowSelect");

        for (int i = 1; i <= total; i++) {
            GetXMLTask task = new GetXMLTask();
            // Execute the task
            task.execute(new String[]{"http://140.121.197.130:8004/Money-Building/CharacterServlet?state=getCharacter&characterID=" + i});
        }
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
            if(count%3 == 0){
                linearLayout = new LinearLayout(Character.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                characterLayout.addView(linearLayout);
            }
            ImageView imageView = new ImageView(Character.this);
            imageView.setImageBitmap(result);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            params.setMargins(10, 20, 10, 20);
            imageView.setLayoutParams(params);
            count++;
            imageView.setId(count);
            imageView.setOnClickListener((View.OnClickListener) Character.this);
            linearLayout.addView(imageView);
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
    @Override
    public void onClick(View v) {
        nowSelect = v.getId();
        Intent intent = new Intent();
        intent.setClass(Character.this, Register.class);
        intent.putExtra("nowSelect", Integer.valueOf(nowSelect));
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();
            intent.setClass(Character.this, Register.class);
            intent.putExtra("nowSelect", nowSelect);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
