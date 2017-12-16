package com.example.user.moneybuilding;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

public class EditBookList extends AppCompatActivity implements View.OnClickListener{


    private List<String> mAppList = new ArrayList<String>();
    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book_list);

        mListView = (SwipeMenuListView) findViewById(R.id.listView);
        mAdapter = new AppAdapter(mAppList);
        mListView.setAdapter(mAdapter);


        initViews();
        initListeners();
        loadData();


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                openItem.setWidth(dp2px(90));
                openItem.setIcon(R.drawable.ic_mode_edit_black_24dp);
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.ic_delete_forever_white_24dp);
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case 1:
                        mAppList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    private void loadData(){
        String item="項目";
        String money="金錢";
        String content="內容";
        String date="日期";
        for (int i = 0; i < 10; i++) {
            mAppList.add(date+" "+money+" "+item+" "+content);
            mAdapter.notifyDataSetChanged();
        }
    }



    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void initViews() {

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetHeading = (TextView) findViewById(R.id.bottomSheetHeading);

        OKBottomSheetButton = (Button) findViewById(R.id.OKbutton);
        cancelBottomSheetButton = (Button) findViewById(R.id.cancelButton);
        showItem=(TextView) findViewById(R.id.moneyItem);

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

    private void initListeners() {
        // register the listener for button click
        OKBottomSheetButton.setOnClickListener(this);
        cancelBottomSheetButton.setOnClickListener(this);

        // Capturing the callbacks for bottom sheet
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetHeading.setText("編輯帳目");
                    bottomSheetHeading.setGravity(Gravity.LEFT);
                }

                // Check Logs to see how bottom sheets behaves
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
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
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

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
                break;


        }
    }


    class AppAdapter extends BaseAdapter {

        private List<String> list ;
        public AppAdapter(List<String> list){
            this.list = list;
        }

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public String getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_list_app, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position);
            holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a1));
            holder.tv_name.setText(list.get(position));

            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);

                view.setTag(this);
            }
        }

        public boolean getSwipEnableByPosition(int position) {
            if(position % 2 == 0){
                return false;
            }
            return true;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();
            intent.setClass(EditBookList.this, MainTallyBook.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

}
