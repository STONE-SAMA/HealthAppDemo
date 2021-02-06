package com.example.healthappdemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthappdemo.DataClass.EnergyInfo;
import com.example.healthappdemo.R;

import java.util.List;

public class MyListViewAdapter extends BaseAdapter {
    List<EnergyInfo> list;
    Context mContext;

    public MyListViewAdapter(List<EnergyInfo> list, Context mContext) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.energy_record_layout,null);
        }else {
            view = convertView;
        }

        EnergyInfo energyInfo = list.get(position);
        ImageView imageView = view.findViewById(R.id.iv_energy_type);//图片
        TextView name = view.findViewById(R.id.tv_energy_name);//名字
        TextView count = view.findViewById(R.id.tv_food_number);//食物份数
        TextView number = view.findViewById(R.id.tv_energy_number);//能量
        TextView energyID = view.findViewById(R.id.tv_energy_record_id);

        imageView.setImageResource(energyInfo.getImg());
        name.setText(energyInfo.getName());
        count.setText(String.valueOf(energyInfo.getCount()));
        number.setText(String.valueOf(energyInfo.getNumber()));
        energyID.setText(energyInfo.getEnergyID());

        if(energyID.getText().toString().substring(0,1).equals("9")){
            TextView tv = view.findViewById(R.id.tv_energy_txt);
            tv.setText("分钟");
        }

        return view;
    }
}
