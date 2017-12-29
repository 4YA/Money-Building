package com.example.user.moneybuilding;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import static android.util.Log.v;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private LinearAdapter mAdapter;
    private RadioGroup endDate;
    private RadioButton endDateYes;
    private RadioGroup target;
    private RadioButton targetYes;
    private boolean deleteButton=false;
    private RequestQueue queue;
    private String userID;
	private int targetMoney;
    private List<String> mDatas = new ArrayList<String>();
    private int mYear, mMonth, mDay;

    private RadioButton targetNo;
    private RadioButton endDateNo;
    private TextView showDate;
    private TextView showTarget;
    private ArrayList<String> tallyBookIDArr; //帳本ID
    private ArrayList<String> tallyBookNameArr; //'帳本名稱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYear =  mMonth = mDay = 0;
        targetMoney = 0;
        tallyBookIDArr = new ArrayList<String>();
        tallyBookNameArr = new ArrayList<String>();
        //startActivity(new Intent(HomePage.this,ListViewActivity.class));
        setContentView(R.layout.activity_home_page);
        queue = Volley.newRequestQueue(this);
        userID = getSharedPreferences("data", MODE_PRIVATE)
                .getString("userID", "");
        FirebaseMessaging.getInstance().subscribeToTopic("userID"+userID);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/"+"PersonalInformationServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            TextView headerName = findViewById(R.id.headerName);
                            headerName.setText(arr.get(1).toString());
                            //取得名字&角色圖片
                            if(!arr.get(0).toString().equals("0")) {
                                ImageView headerImage = findViewById(R.id.headerImage);
                                Picasso.with(HomePage.this).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=" + arr.get(0).toString()).transform(new CircleTransform())
                                        .into(headerImage);
                            }
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
                map.put("state", "getHeaderData");
                map.put("userID", userID);
                return map;
            }
        };

        StringRequest stringRequest2  = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/"+"GetTallyBookServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            for(int s=0;s<arr.length();s++){
                                addData(1);
                                JSONObject temp = arr.getJSONObject(s);
                                getTallyBookFromServer(temp);
                            }
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
                map.put("state", "getTallyBook");
                map.put("userID", getSharedPreferences("data", MODE_PRIVATE).getString("userID",""));

                return map;
            }
        };

        queue.add(stringRequest);   //把request丟進queue(佇列)
        queue.add(stringRequest2);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        changeTallybook();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void getTallyBookFromServer(JSONObject temp) throws JSONException {
        tallyBookIDArr.add(temp.getString("tallyBookID"));
        tallyBookNameArr.add(temp.getString("tallyBookName"));
        Log.d("tallyBookID",temp.getString("tallyBookID"));
    }
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space=space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left=space;
            outRect.right=space;
            outRect.bottom=space;
            if(parent.getLayoutManager() instanceof GridLayoutManager || parent.getLayoutManager() instanceof StaggeredGridLayoutManager){
                if(parent.getChildAdapterPosition(view) < 2){
                    outRect.top=space;
                }
            }else{
                if(parent.getChildAdapterPosition(view)==0){
                    outRect.top=space;
                }
            }
        }
    }


    public void changeTallybook(){
       final FabSpeedDial fabSpeedDialMain = (FabSpeedDial) findViewById(R.id.fab_speed_dial_in_main);

        fabSpeedDialMain.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
               // if(!deleteButton) {
                    switch (menuItem.getItemId()) {
                        case R.id.add_tallybook_by_click:
                            changeBack();
                            //if(!deleteButton) {
                            LayoutInflater inflater = LayoutInflater.from(HomePage.this);
                            final View dialogName = inflater.inflate(R.layout.new_tallybook, null);

                            endDate = (RadioGroup) dialogName.findViewById(R.id.endDate);
                            endDateYes = (RadioButton) dialogName.findViewById(R.id.endDateYes);
                            target = (RadioGroup) dialogName.findViewById(R.id.target);
                            targetYes = (RadioButton) dialogName.findViewById(R.id.targetYes);
                            endDate.setOnCheckedChangeListener(listenerEndDate);
                            target.setOnCheckedChangeListener(listenerTarget);
                            targetNo = (RadioButton) dialogName.findViewById(R.id.targetNo);
                            endDateNo = (RadioButton) dialogName.findViewById(R.id.endDateNo);
                            showDate=(TextView) dialogName.findViewById(R.id.show_date);
                            showTarget=(TextView) dialogName.findViewById(R.id.show_target_money);

                            new AlertDialog.Builder(HomePage.this)
                                    .setTitle("新增帳本")
                                    .setView(dialogName)
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            EditText nameText = (EditText) dialogName.findViewById(R.id.editTallybookName);
                                            if (nameText.getText().toString().equals("")) {
                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);
                                                alertDialog.setTitle("提醒");
                                                alertDialog.setMessage("需填入帳本名稱!");
                                                alertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                    }
                                                });
                                                alertDialog.show();

                                            } else {
                                                addData(1);
                                                addToServer(nameText.getText().toString());
                                            }
                                        }
                                    })
                                    .show();
                            //}//
                            return true;
                        case R.id.add_tallybook_by_other:
                            changeBack();
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);
                            alertDialog.setTitle("選擇加入現有帳本方式");
                            alertDialog.setPositiveButton("輸入ID加入帳本", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    LayoutInflater inflater = LayoutInflater.from(HomePage.this);
                                    final View dialogID = inflater.inflate(R.layout.input_id, null);

                                    new AlertDialog.Builder(HomePage.this)
                                            .setTitle("加入帳本")
                                            .setView(dialogID)
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    EditText nameText = (EditText) dialogID.findViewById(R.id.id_Input);
                                                    final String ID = nameText.getText().toString();
                                                    if (ID.equals("")) {
                                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);
                                                        alertDialog.setTitle("提醒");
                                                        alertDialog.setMessage("需填入帳本ID!");
                                                        alertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface arg0, int arg1) {
                                                            }
                                                        });
                                                        alertDialog.show();

                                                    } else {
                                                        StringRequest stringRequest  = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/"+"JoinTallyBookServlet",
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                         try{
                                                                            JSONObject obj = new JSONObject(response);
                                                                             Log.d("objTostring",obj.toString());


                                                                            if(obj.has("isEmpty")){

                                                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);
                                                                                alertDialog.setTitle("提醒");
                                                                                alertDialog.setMessage("查無此ID!");
                                                                                alertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                                                    }
                                                                                });
                                                                                alertDialog.show();
                                                                            }
                                                                            else if(obj.has("isDuplicated")){

                                                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);
                                                                                alertDialog.setTitle("提醒");
                                                                                alertDialog.setMessage("你已加入過此帳本!");
                                                                                alertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                                                    }
                                                                                });
                                                                                alertDialog.show();
                                                                            }
                                                                            else{
                                                                                getTallyBookFromServer(obj);
                                                                                AlertDialog.Builder d1 = new AlertDialog.Builder(HomePage.this);
                                                                                d1.setTitle("提醒");
                                                                                d1.setMessage("加入成功!");
                                                                                d1.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                                                        addData(1);
                                                                                    }
                                                                                });
                                                                                d1.show();

                                                                            }

                                                                        } catch (Throwable t) {
                                                                        }
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            // @Override
                                                            public void onErrorResponse(VolleyError error) {    //錯誤訊息
                                                                Log.d("error",error.toString());
                                                            }
                                                        }) {
                                                            @Override
                                                            protected Map<String, String> getParams() {
                                                                Map<String, String> map = new HashMap<String, String>();
                                                                map.put("state", "getTallyBookByID");
                                                                map.put("userID", getSharedPreferences("data", MODE_PRIVATE).getString("userID",""));
                                                                map.put("tallyBookID", ID);

                                                                return map;
                                                            }
                                                        };
                                                        queue.add(stringRequest);
                                                    }
                                                }
                                            })
                                            .show();
                                }
                            });
                            alertDialog.setNeutralButton("以QR Code加入帳本", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    IntentIntegrator integrator = new IntentIntegrator(HomePage.this);
                                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                                    integrator.setCameraId(0);
                                    integrator.setBeepEnabled(false);
                                    integrator.setBarcodeImageEnabled(false);
                                    integrator.initiateScan();
                                }
                            });
                            alertDialog.show();
                            return true;
                        case R.id.delete_tallybook:
                            if(mDatas.size()!=0){
                                deleteButton=true;
                                changePicture(R.drawable.plus_del,"gray");
                            }
                            return true;
                    }
               // }
                return false;
            }
        });
    }
    //掃描QRCode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        RequestQueue queue = Volley.newRequestQueue(this);
        if(result !=null){
            if(result.getContents() == null){
                Toast.makeText(this,"You can't celled the scanning",Toast.LENGTH_SHORT).show();;
            }else {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/"+"JoinTallyBookServlet",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject obj = new JSONObject(response);
                                    Log.d("objTostring",obj.toString());


                                    if(obj.has("isEmpty")){

                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);
                                        alertDialog.setTitle("提醒");
                                        alertDialog.setMessage("查無此ID!");
                                        alertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                    else if(obj.has("isDuplicated")){

                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);
                                        alertDialog.setTitle("提醒");
                                        alertDialog.setMessage("你已加入過此帳本!");
                                        alertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                    else{
                                        getTallyBookFromServer(obj);
                                        AlertDialog.Builder d1 = new AlertDialog.Builder(HomePage.this);
                                        d1.setTitle("提醒");
                                        d1.setMessage("加入成功!");
                                        d1.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                addData(1);
                                            }
                                        });
                                        d1.show();

                                    }

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
                        String QRCode = result.getContents();
                        map.put("state", "getTallyBookByQRCode");
                        map.put("userID", getSharedPreferences("data", MODE_PRIVATE).getString("userID",""));
                        map.put("QRCode", QRCode);
                        return map;
                    }
                };
                queue.add(stringRequest);   //把request丟進queue(佇列)
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void changePicture(int url,String backgroundColor){
        int tint = Color.parseColor(backgroundColor);
        for(int i=0;i<mDatas.size();i++){
            View imView= mRecyclerView.getLayoutManager().findViewByPosition(i);
            ImageButton changePic = (ImageButton) imView.findViewById(R.id.adapter_linear_text);
            changePic.setImageResource(url);
        }
    }

    public void changeBack(){
            deleteButton=false;
            changePicture(R.drawable.plus,"#FF4081");
    }

    private RadioGroup.OnCheckedChangeListener listenerEndDate = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(endDateYes.isChecked()){
                showDate.setVisibility( View.VISIBLE );
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(HomePage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
							mYear = year;
                            mMonth = month+1;
                            mDay = day;
                            String dateText=mYear+"/"+mMonth+"/"+mDay;
                            showDate.setText(dateText);
                    }

                }, mYear,mMonth, mDay)
                        .show();

            }
            if(endDateNo.isChecked()){
                showDate.setText("");
                showDate.setVisibility( View.GONE );
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listenerTarget = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //Log.d("myTag", "This is my message"+endDateYes.isChecked());
            if(targetYes.isChecked()){
                showTarget.setVisibility( View.VISIBLE );
                LayoutInflater inflater = LayoutInflater.from(HomePage.this);
                final View dialogName = inflater.inflate(R.layout.target_money, null);
                new AlertDialog.Builder(HomePage.this)
                        .setTitle("輸入目標金額(0代表不設定)")
                        .setView(dialogName)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
 
                                EditText editTargetMoney =   (EditText) dialogName.findViewById(R.id.moneyInput);
 
                                targetMoney =  Integer.parseInt(editTargetMoney.getText().toString());
                                showTarget.setText("$"+editTargetMoney.getText());

                            }
                        })
                        .show();
            }
            if(targetNo.isChecked()){
                showTarget.setText("");
                showTarget.setVisibility( View.GONE );
            }

        }
    };



    public void addData(int position) {
        if(mDatas.size()==0){
            //initData();
            mDatas.add("Insert" + 0);

            mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);

            mAdapter = new LinearAdapter(mDatas);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new HomePage.SpacesItemDecoration(27));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        }else{
            position=mDatas.size();
            mDatas.add("Insert" + position);
            mAdapter.notifyItemInserted(position);

        }



    }

    public void addToServer(final String nameText){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/AddTallyBookServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            getTallyBookFromServer(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {    //錯誤訊息
                //如果沒連成功錯誤會到這裡
                Log.d("connection error",error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<String, String>();

                map.put("state", "newTallyBook");
                map.put("userID",  getSharedPreferences("data", MODE_PRIVATE).getString("userID",""));
                map.put("name",nameText);
                map.put("year",Integer.toString(mYear));
                map.put("month",Integer.toString(mMonth));
                map.put("day",Integer.toString(mDay));
                map.put("targetMoney",Integer.toString(targetMoney));
                Log.d("test",nameText);
                //放要傳到servlet的資料
                return map;
            }
        };
        queue.toString();
        queue.add(stringRequest);   //把request丟進queue(佇列)
    }

    public void removeData(int position) {
        mDatas.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void removeFromServer(final String ID){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/LeaveTallyBookServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //收到的資料
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {    //錯誤訊息
                //如果沒連成功錯誤會到這裡
                Log.d("connection error",error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<String, String>();

                map.put("state", "LeaveTallyBook");
                map.put("userID",  getSharedPreferences("data", MODE_PRIVATE).getString("userID",""));
                map.put("tallyBookID", ID);
                //放要傳到servlet的資料
                return map;
            }
        };
        queue.toString();
        queue.add(stringRequest);   //把request丟進queue(佇列)
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // Handle the camera action
            SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
            pref.edit()
                    .clear()
                    .commit();
            FirebaseMessaging.getInstance().unsubscribeFromTopic("userID"+userID);
            Intent intent = new Intent();
            intent.setClass(HomePage.this, Login.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_tallybook) {

        } else if (id == R.id.nav_user) {
            Intent intent = new Intent();
            intent.setClass(HomePage.this, PersonalInformation.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_manage) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(HomePage.this)
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

    class LinearAdapter extends RecyclerView.Adapter<HomePage.LinearAdapter.MyViewHolder> {

        private List<String> list ;
        public LinearAdapter(List<String> list){
            this.list = list;
        }
        @Override
        public HomePage.LinearAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            HomePage.LinearAdapter.MyViewHolder holder = new HomePage.LinearAdapter.MyViewHolder(LayoutInflater.from(HomePage.this).inflate(R.layout.item_linear,parent,false));

           return holder;
        }



        @Override
        public void onBindViewHolder(HomePage.LinearAdapter.MyViewHolder holder, int position) {

            holder.tv.setContentDescription(list.get(position));
            holder.tv.setImageResource(R.drawable.plus);
        }


        @Override
        public int getItemCount() {

            return mDatas.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageButton tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (ImageButton) itemView.findViewById(R.id.adapter_linear_text);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(deleteButton){
                            AlertDialog.Builder dialog = new AlertDialog.Builder(HomePage.this);
                            dialog.setTitle("提醒");
                            dialog.setMessage("確定刪除帳本?");
                            dialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {}
                            });
                            dialog.setPositiveButton("確定",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    int index = getLayoutPosition();

                                     removeFromServer(tallyBookIDArr.get(index));

                                    removeData(index);


                                }
                            });
                            dialog.show();
                            changeBack();
                        }else{
                            Intent intent = new Intent();
                            intent.setClass(HomePage.this, MainTallyBook.class);
                            Bundle bundle = new Bundle();
                            int index = getLayoutPosition();
                            bundle.putString("tallyBookID",tallyBookIDArr.get(index));
                            bundle.putString("back","No");
                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();

                        }
                    }
                });
            }
        }
    }
}
