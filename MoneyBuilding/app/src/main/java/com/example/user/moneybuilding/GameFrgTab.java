package com.example.user.moneybuilding;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class GameFrgTab extends Fragment {
    private View rootView;
    private View memberView;
    private List<String> mGameAppList = new ArrayList<String>();
    private AppAdapter2 mGameAdapter;
    private SwipeMenuListView mGameListView;
    private Button member;
    private Integer money;
    private Integer objective;
    private Integer level;
    private TextView hint;
    private ArrayList<String> memberIcon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_tab, container, false);
        //memberView = inflater.inflate(R.layout.show_member, container, false);

        member = (Button) rootView.findViewById(R.id.push_button);

        hint = (TextView)rootView.findViewById(R.id.hint);
        resetGame();
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
    public void createMember(String name,String icon){//String name


        mGameAdapter = new AppAdapter2(mGameAppList);

        if(memberIcon == null){
            memberIcon = new ArrayList<String>();
        }
        mGameAppList.add(name);//name
        memberIcon.add(icon);

        mGameAdapter.notifyDataSetChanged();


    }

    public void resetGame(){
        hint.setText(Html.fromHtml("目前資金:"+money.toString()+"<br>預期資金:"+objective.toString() + "<br>房子等級:"+level.toString()));

        switch(level){
            case 0: ((ImageView)rootView.findViewById(R.id.imageView3)).setImageDrawable(getResources().getDrawable(R.drawable.level1));break;
            case 1: ((ImageView)rootView.findViewById(R.id.imageView3)).setImageDrawable(getResources().getDrawable(R.drawable.level2));break;
            case 2: ((ImageView)rootView.findViewById(R.id.imageView3)).setImageDrawable(getResources().getDrawable(R.drawable.level3));break;
            case 3: ((ImageView)rootView.findViewById(R.id.imageView3)).setImageDrawable(getResources().getDrawable(R.drawable.level4));break;
            default: ((ImageView)rootView.findViewById(R.id.imageView3)).setImageDrawable(getResources().getDrawable(R.drawable.level5));break;
        }
        ((ImageView)rootView.findViewById(R.id.imageView3)).setZ(-1);
    }

    public void writeHint(Integer money,Integer objective,Integer level){
        this.money = money;
        this.objective = objective;
        this.level = level;

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



    class AppAdapter2 extends BaseAdapter {

        private List<String> list ;
        public AppAdapter2(List<String> list){
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
                        R.layout.item_list_member, null);
                new AppAdapter2.ViewHolder(convertView);
            }
            AppAdapter2.ViewHolder holder = (AppAdapter2.ViewHolder) convertView.getTag();
            String item = getItem(position);
            Log.d("AA",((Integer)position).toString());
            switch(memberIcon.get(position)){
                case "1":
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=1").transform(new CircleTransform())
                        .into(holder.iv_icon);break;
                case "2":
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=2").transform(new CircleTransform())
                            .into(holder.iv_icon);break;
                case "3":
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=3").transform(new CircleTransform())
                            .into(holder.iv_icon);break;
                case "4":
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=4").transform(new CircleTransform())
                            .into(holder.iv_icon);break;
                case "5":
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=5").transform(new CircleTransform())
                            .into(holder.iv_icon);break;
                case "6":
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=6").transform(new CircleTransform())
                            .into(holder.iv_icon);break;
                case "7":
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=7").transform(new CircleTransform())
                            .into(holder.iv_icon);break;
                case "8":
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=8").transform(new CircleTransform())
                            .into(holder.iv_icon);break;

                default:
                    Picasso.with(convertView.getContext()).load(getString(R.string.servletURL) + "CharacterServlet?state=getCharacter&characterID=8").transform(new CircleTransform())
                            .into(holder.iv_icon);break;


            }

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
