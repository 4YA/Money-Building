package com.example.user.moneybuilding;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;


public class MainTallyBook extends AppCompatActivity implements View.OnClickListener{

    private BottomSheetBehavior bottomSheetBehavior;

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
    private RadioGroup target;
    private RadioButton targetYes;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tally_book);

        initViews();
        initListeners();

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
                        target = (RadioGroup) dialogName.findViewById(R.id.target);
                        targetYes = (RadioButton) dialogName.findViewById(R.id.targetYes);
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
                                    }
                                })
                                .show();
                        return true;
                    case R.id.edit_item:
                        Intent intent = new Intent();
                        intent.setClass(MainTallyBook.this, EditBookList.class);
                        startActivity(intent);
                        finish();
                        return true;
                }


                return false;
            }
        });
    }

    private void initViews() {


        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetHeading = (TextView) findViewById(R.id.bottomSheetHeading);
        OKBottomSheetButton = (Button) findViewById(R.id.OKbutton);
        cancelBottomSheetButton = (Button) findViewById(R.id.cancelButton);
        showItem=(TextView) findViewById(R.id.moneyItem);

        bottomSheetHeading.setText("+新紀錄");
        bottomSheetHeading.setGravity(Gravity.CENTER);
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
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(MainTallyBook.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                    }

                }, mYear,mMonth, mDay)
                        .show();
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listenerTarget = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //Log.d("myTag", "This is my message"+endDateYes.isChecked());
            if(targetYes.isChecked()){
                LayoutInflater inflater = LayoutInflater.from(MainTallyBook.this);
                final View dialogName = inflater.inflate(R.layout.target_money, null);
                new AlertDialog.Builder(MainTallyBook.this)
                        .setTitle("輸入目標金額")
                        .setView(dialogName)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }

        }
    };

    /**
     * method to initialize the listeners
     */
    private void initListeners() {
        // register the listener for button click
        bottomSheetHeading.setOnClickListener(this);
        OKBottomSheetButton.setOnClickListener(this);
        cancelBottomSheetButton.setOnClickListener(this);

        // Capturing the callbacks for bottom sheet
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetHeading.setText("新增帳目");
                    bottomSheetHeading.setGravity(Gravity.LEFT);
                }else{
                    bottomSheetHeading.setText("+新紀錄");
                    bottomSheetHeading.setGravity(Gravity.CENTER);
                }

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
            case R.id.bottomSheetHeading:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.cancelButton:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.OKbutton:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;


        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();
            intent.setClass(MainTallyBook.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
