package com.example.user.moneybuilding;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class EditBookList extends Fragment {


    private List<String> mAppList = new ArrayList<String>();
    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;
    private ArrayList<String> recordID = new ArrayList<String>();
    private RequestQueue queue;


    public void loadData(String type,String date,String money,String content){
            mAppList.add(type+" "+money+" "+content+" "+date);

            mAdapter.notifyDataSetChanged();
    }

    public void pushRecordID(String id){
        recordID.add(id);
    }


    private View rootView;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_edit_book_list, container, false);
        
        mListView = (SwipeMenuListView) rootView.findViewById(R.id.listView);
        mAdapter = new AppAdapter(mAppList);
        mListView.setAdapter(mAdapter);
       // initViews();
       // initListeners();
        queue = Volley.newRequestQueue(this.getContext());


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                openItem.setWidth(dp2px(90));
                openItem.setIcon(R.drawable.ic_mode_edit_black_24dp);
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
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
                        ((MainTallyBook)getActivity()).open();
                        break;
                    case 1:
                        mAppList.remove(position);
                        deleteDataFromServer(recordID.get(position));
                        recordID.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        return rootView;
    }
    public void deleteDataFromServer(final String recordID) {

        StringRequest request = new StringRequest(Request.Method.POST, "http://140.121.197.130:8901/Money-Building/" + "DeleteRecordServlet",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

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
                map.put("state", "deleteRecord");
                map.put("recordID", recordID);
                return map;
            }
        };

        queue.add(request);
    }



    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
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
                convertView = View.inflate(getContext(),
                        R.layout.item_list_app, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position);
            String str[] =  list.get(position).split(" ");
            switch(str[0]) {
                case "交通":
                    holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a1));
                    holder.tv_name.setTextColor(getResources().getColor(R.color.colorGreen));
                    break;
                case "購物":
                    holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a2));
                    holder.tv_name.setTextColor(getResources().getColor(R.color.colorGreen));
                    break;
                case "收入":
                    holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a3));
                    holder.tv_name.setTextColor(getResources().getColor(R.color.colorRed));
                    break;
                case "娛樂":
                    holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a4));
                    holder.tv_name.setTextColor(getResources().getColor(R.color.colorGreen));
                    break;
                case "飲食":
                    holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a5));
                    holder.tv_name.setTextColor(getResources().getColor(R.color.colorGreen));
                    break;
                case "電信":
                    holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a6));
                    holder.tv_name.setTextColor(getResources().getColor(R.color.colorGreen));
                    break;
                case "醫療":
                   holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a7));
                    holder.tv_name.setTextColor(getResources().getColor(R.color.colorGreen));
                    break;
                case "其他":
                    holder.iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.a8));
                    holder.tv_name.setTextColor(getResources().getColor(R.color.colorGreen));
                    break;

            }
            holder.tv_name.setText(str[1]+ "元");
            holder.tv_content.setText(str[2]);
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            TextView tv_content;
            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                tv_content = (TextView) view.findViewById(R.id.tv_content);
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


}




