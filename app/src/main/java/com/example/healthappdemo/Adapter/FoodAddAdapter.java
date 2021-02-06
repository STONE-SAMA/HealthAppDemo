package com.example.healthappdemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthappdemo.DataClass.FoodInfo;
import com.example.healthappdemo.R;

import java.util.List;

public class FoodAddAdapter extends BaseAdapter {
    List<FoodInfo> list;
    Context mContext;

    public FoodAddAdapter(List<FoodInfo> list, Context mContext) {
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
        if(convertView == null ){
            view = LayoutInflater.from(mContext).inflate(R.layout.food_add_layout,null);
        }else{
            view = convertView;
        }

        FoodInfo foodInfo = list.get(position);
        ImageView foodType = view.findViewById(R.id.iv_layout_food_type);
        TextView foodName = view.findViewById(R.id.tv_layout_food_name);
        TextView foodEnergy = view.findViewById(R.id.tv_layout_food_energy);
        ImageView foodLevel = view.findViewById(R.id.iv_layout_food_point);
        TextView foodID = view.findViewById(R.id.tv_food_add_id);


        foodType.setImageResource(foodInfo.getFood_img());
        foodName.setText(foodInfo.getName());
        foodEnergy.setText(String.valueOf(foodInfo.getEnergy()));
        foodLevel.setImageResource(foodInfo.getLevel_img());
        foodID.setText(foodInfo.getFood_id());

        return view;
    }
}
