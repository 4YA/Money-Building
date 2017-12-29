package com.example.user.moneybuilding;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;


public class GameFrgTab extends Fragment {
    private View rootView;
    private View memberView;
    private List<String> mGameAppList = new ArrayList<String>();
    private AppAdapter mGameAdapter;
    private SwipeMenuListView mGameListView;
    private Button member;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_tab, container, false);
        //memberView = inflater.inflate(R.layout.show_member, container, false);

        member = (Button) rootView.findViewById(R.id.push_button);



        member.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View memberView = inflater.inflate(R.layout.show_member, null);

                mGameListView = (SwipeMenuListView) memberView.findViewById(R.id.memberListView);
                if(mGameAdapter==null){
                    return;
                }
                mGameListView.setAdapter(mGameAdapter);
                new AlertDialog.Builder(getContext())
                        .setTitle("帳本成員")
                        .setView(memberView)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }

        });

        return rootView;
    }
    public void createMember(String name){//String name


        mGameAdapter = new AppAdapter(mGameAppList);


        mGameAppList.add(name);//name
        mGameAdapter.notifyDataSetChanged();


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent();
            intent.setClass(getActivity(), HomePage.class);
            startActivity(intent);
            getActivity().finish();
        }
        return true;
    }



    class AppAdapter extends BaseAdapter {

        private List<String> list ;
        public AppAdapter(List<String> list){
            this.list = list;
        }

        @Override
        public int getCount() {
            return mGameAppList.size();
        }

        @Override
        public String getItem(int position) {
            return mGameAppList.get(position);
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
                new AppAdapter.ViewHolder(convertView);
            }
            AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) convertView.getTag();
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

}
