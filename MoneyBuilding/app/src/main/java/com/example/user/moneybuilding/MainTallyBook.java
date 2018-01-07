package com.example.user.moneybuilding;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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



public class MainTallyBook extends AppCompatActivity implements View.OnClickListener{

    public BottomSheetBehavior bottomSheetBehavior;

    // TextView variable
    private TextView bottomSheetHeading;
    private TextView showItem;
    private Button OKBottomSheetButton;
    private Button cancelBottomSheetButton;
    private ImageButton Button_1;
    private ImageButton Button_2;
    private ImageButton Button_3;
    private ImageButton Button_4;
    private ImageButton Button_5;
    private ImageButton Button_6;
    private ImageButton Button_7;
    private ImageButton Button_8;

    private RadioGroup endDate;
    private RadioButton endDateYes;
    private RadioButton endDateNo;
    private TextView showDate;
    private RadioGroup target;
    private RadioButton targetYes;
    private RadioButton targetNo;
    private TextView showTarget;
    private int mYear, mMonth, mDay;
    private LockableViewPager mViewPager;
    private String tallyBookID;
    private RequestQueue queue;
    private GameFrgTab game;
    private EditBookList recordList;
    private ArrayList<String> memberName;
    private int targetMoney = 0;
    private Integer balance = 0;
    private Integer objective = 0;
    private Integer level = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tally_book);
        Bundle bundle =this.getIntent().getExtras();
        game = new GameFrgTab();
        recordList = new EditBookList();
        queue = Volley.newRequestQueue(this);
        tallyBookID = bundle.getString("tallyBookID");
        objective = Integer.parseInt(bundle.getString("tallyBookObjective"));
        level = Integer.parseInt(bundle.getString("tallyBookLevel"));
        balance = Integer.parseInt(bundle.getString("tallyBookMoney"));
        memberName = new ArrayList<String>();


        game.writeHint(balance,objective,level);

        StringRequest request = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/"+"GetTallyBookServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);

                            for(int i=0;i<arr.length();i++){

                                game.createMember((String)arr.get(i));
                                memberName.add((String)arr.get(i));
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
                map.put("state", "getTallyBookMember");
                map.put("tallyBookID", tallyBookID);
                return map;
            }
        };



        StringRequest request2 = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/"+"GetRecordServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);

                            for(int i=0;i<arr.length();i++){
                                JSONObject temp = arr.getJSONObject(i);
                                Log.d("SS",temp.toString());
                                getRecordFromServer(temp);

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
                map.put("state", "getRecord");
                map.put("tallyBookID", tallyBookID);
                return map;
            }
        };

        queue.add(request);
        queue.add(request2);


        initViews();
        initListeners();

        initToolbar(bundle.getString("tallyBookName"));
        Log.d("balance",((Integer)balance).toString());
        initTabLayout();

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.edit_talltbook:
                        LayoutInflater inflater = LayoutInflater.from(MainTallyBook.this);
                        final View dialogName = inflater.inflate(R.layout.new_tallybook, null);

                        endDate = (RadioGroup) dialogName.findViewById(R.id.endDate);
                        endDateYes = (RadioButton) dialogName.findViewById(R.id.endDateYes);
                        endDateNo = (RadioButton) dialogName.findViewById(R.id.endDateNo);
                        target = (RadioGroup) dialogName.findViewById(R.id.target);
                        targetYes = (RadioButton) dialogName.findViewById(R.id.targetYes);
                        targetNo = (RadioButton) dialogName.findViewById(R.id.targetNo);
                        showTarget=(TextView) dialogName.findViewById(R.id.show_target_money);
                        showDate=(TextView) dialogName.findViewById(R.id.show_date);
                        endDate.setOnCheckedChangeListener(listenerEndDate);
                        target.setOnCheckedChangeListener(listenerTarget);

                        new AlertDialog.Builder(MainTallyBook.this)
                                .setTitle("編輯帳本")
                                .setView(dialogName)
                                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditText nameText = (EditText) dialogName.findViewById(R.id.editTallybookName);
                                        if (nameText.getText().toString().equals("")) {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainTallyBook.this);
                                            alertDialog.setTitle("提醒");
                                            alertDialog.setMessage("需填入帳本名稱!");
                                            alertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                }
                                            });
                                            alertDialog.show();

                                        }
                                        else{
                                            editTallyBook(nameText.getText().toString());
                                        }
                                    }
                                })
                                .show();
                        return true;
                    case R.id.plus_item:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        return true;
                }


                return false;
            }
        });
    }

    private void initToolbar(String name) {
        getSupportActionBar().setTitle(name);
    }

    public void getRecordFromServer(JSONObject temp) throws JSONException {
        recordList.pushRecordID(temp.getString("recordID"));
        recordList.loadData(temp.getString("type"),temp.getString("dateTime"),temp.getString("money"),temp.getString("content"));

    }


    public void editTallyBook(final String nameText){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/EditTallyBookServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        initToolbar(nameText);
                        objective = targetMoney;
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

                map.put("state", "editTallyBook");
                map.put("tallyBookID",  tallyBookID);
                map.put("name",nameText);
                map.put("year",Integer.toString(mYear));
                map.put("month",Integer.toString(mMonth));
                map.put("day",Integer.toString(mDay));
                map.put("targetMoney",Integer.toString(targetMoney));

                //放要傳到servlet的資料
                return map;
            }
        };

        queue.add(stringRequest);   //把request丟進queue(佇列)
    }

    private void initTabLayout(){
        mViewPager = (LockableViewPager) findViewById(R.id.photosViewPager);
        mViewPager.setSwipeable(false);


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new TabFragment(), "Title 1");
//        adapter.addFragment(new TabFragment(), "Title 2");

        mViewPager.setAdapter(adapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initViews() {


        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        OKBottomSheetButton = (Button) findViewById(R.id.OKbutton);
        cancelBottomSheetButton = (Button) findViewById(R.id.cancelButton);
        showItem=(TextView) findViewById(R.id.moneyItem);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //bottomSheetHeading.setText("+新紀錄");
        //bottomSheetHeading.setGravity(Gravity.CENTER);
        initItemBotton();

    }

    private void initItemBotton(){
        Button_1 = (ImageButton) findViewById(R.id.imageButton1);
        Button_2 = (ImageButton) findViewById(R.id.imageButton2);
        Button_3 = (ImageButton) findViewById(R.id.imageButton3);
        Button_4 = (ImageButton) findViewById(R.id.imageButton4);
        Button_5 = (ImageButton) findViewById(R.id.imageButton5);
        Button_6 = (ImageButton) findViewById(R.id.imageButton6);
        Button_7 = (ImageButton) findViewById(R.id.imageButton7);
        Button_8 = (ImageButton) findViewById(R.id.imageButton8);
        Button_1.setOnClickListener(this);
        Button_2.setOnClickListener(this);
        Button_3.setOnClickListener(this);
        Button_4.setOnClickListener(this);
        Button_5.setOnClickListener(this);
        Button_6.setOnClickListener(this);
        Button_7.setOnClickListener(this);
        Button_8.setOnClickListener(this);
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
                new DatePickerDialog(MainTallyBook.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mYear = year;
                        month += 1;
                        mMonth = month;
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
            if(targetYes.isChecked()){
                showTarget.setVisibility( View.VISIBLE );
                LayoutInflater inflater = LayoutInflater.from(MainTallyBook.this);
                final View dialogName = inflater.inflate(R.layout.target_money, null);
                new AlertDialog.Builder(MainTallyBook.this)
                        .setTitle("輸入目標金額")
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

    /**
     * method to initialize the listeners
     */
    private void initListeners() {
        // register the listener for button click
        OKBottomSheetButton.setOnClickListener(this);
        cancelBottomSheetButton.setOnClickListener(this);

        // Capturing the callbacks for bottom sheet
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                // Check Logs to see how bottom sheets behaves
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;

                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });


    }

    public void open(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * onClick Listener to capture button click
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton1:
                showItem.setText(R.string.buttom_1_name);
                break;
            case R.id.imageButton2:
                showItem.setText(R.string.buttom_2_name);
                break;
            case R.id.imageButton3:
                showItem.setText(R.string.buttom_3_name);
                break;
            case R.id.imageButton4:
                showItem.setText(R.string.buttom_4_name);
                break;
            case R.id.imageButton5:
                showItem.setText(R.string.buttom_5_name);
                break;
            case R.id.imageButton6:
                showItem.setText(R.string.buttom_6_name);
                break;
            case R.id.imageButton7:
                showItem.setText(R.string.buttom_7_name);
                break;
            case R.id.imageButton8:
                showItem.setText(R.string.buttom_8_name);
                break;
            case R.id.cancelButton:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            case R.id.OKbutton:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                addRecordToServer();
                break;


        }
    }


    public void  addRecordToServer(){
        EditText moneyText = (EditText)findViewById(R.id.moneyText);
        String money = moneyText.getText().toString();
        EditText editMoneyItem = (EditText)findViewById(R.id.editMoneyItem);
        final  String edit  = editMoneyItem.getText().toString();
        final String type = showItem.getText().toString();
        final String mmoney;
        if(money == null){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainTallyBook.this);
            alertDialog.setTitle("提醒");
            alertDialog.setMessage("請輸入金額!");
            alertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            alertDialog.show();
            return;
        }

        if(type.equals("收入"))
            mmoney = money;
        else
            mmoney = "-"+money;


        final Calendar c = Calendar.getInstance();
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH)+1);
        String day = Integer.toString(c.get(Calendar.DATE)+1);

        recordList.loadData(type,year+"-"+month+"-"+day,mmoney,edit);

        StringRequest request = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/"+"AddRecordServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j = new JSONObject(response);
                            recordList.pushRecordID(j.getString("recordID"));
                            objective =  Integer.parseInt(j.getString("objective"));
                            balance =  Integer.parseInt(j.getString("balance"));
                            level =  Integer.parseInt(j.getString("level"));
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
                map.put("state", "newRecord");
                map.put("tallyBookID", tallyBookID);
                map.put("money", mmoney);
                map.put("edit", edit);
                map.put("type", type);
                map.put("userID",  getSharedPreferences("data", MODE_PRIVATE).getString("userID",""));
                map.put("public",  "0");

                return map;
            }
        };

        queue.add(request);
        game.writeHint(balance,objective,level);
        game.resetGame();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Bundle bundle = this.getIntent().getExtras();
            if(bundle.getString("back").equals("Yes")) {
                finish();
            }else {
                Intent intent = new Intent();
                intent.setClass(MainTallyBook.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        }
        return true;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);



            mFragmentList.add(game);
            mFragmentList.add(recordList);

            mFragmentTitleList.add("遊戲畫面");
            mFragmentTitleList.add("我的帳目");
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tally_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_qrcode) {
            // do something here
            showQRCodeModal();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showQRCodeModal() {
        RequestQueue q = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/"+"GetTallyBookServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            WebView myWebView = new WebView(MainTallyBook.this);
                            myWebView.loadUrl(arr.get(1).toString());
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainTallyBook.this);
                            builder.setTitle(arr.get(0).toString())
                                    .setView(myWebView)
                                    .setIcon(R.drawable.qrcode3)
                                    .setPositiveButton("OK", null)
                                    .show();
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
                map.put("state", "showTallyBookQRCode");
                map.put("tallyBookID", tallyBookID);
                return map;
            }
        };
        q.add(request);
    }
}
