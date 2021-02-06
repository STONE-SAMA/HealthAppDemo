package com.example.healthappdemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthappdemo.R;
import com.example.healthappdemo.DataClass.SportInfo;

import java.util.List;

public class SportAddAdapter extends BaseAdapter {
    List<SportInfo> list;
    Context mContext;

    public SportAddAdapter(List<SportInfo> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.sport_add_layout,null);
        }else {
            view = convertView;
        }

        SportInfo sportInfo = list.get(position);
        ImageView sportType = view.findViewById(R.id.iv_layout_sport_type);
        TextView sportName = view.findViewById(R.id.tv_layout_sport_name);
        TextView sportEnergy = view.findViewById(R.id.tv_layout_sport_energy);
        TextView sportID = view.findViewById(R.id.tv_sport_add_id);

        sportType.setImageResource(sportInfo.getSport_img());
        sportName.setText(sportInfo.getName());
        sportEnergy.setText(String.valueOf(sportInfo.getEnergy()));
        sportID.setText(sportInfo.getSportID());


        return view;
    }
}
