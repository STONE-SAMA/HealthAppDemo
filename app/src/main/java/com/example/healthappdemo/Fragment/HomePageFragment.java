package com.example.healthappdemo.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthappdemo.Activity.FoodRecordActivity;
import com.example.healthappdemo.Adapter.MyListViewAdapter;
import com.example.healthappdemo.DBHelper;
import com.example.healthappdemo.DataClass.EnergyInfo;
import com.example.healthappdemo.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomePageFragment extends Fragment {
    private TextView tv_userName;//用户名
    private TextView tv_stage;//当前阶段
    private TextView tv_current_KG;//当前体重
    private TextView tv_BMI;//BMI指数
    private double weight;
    private double height;
    private double BMI;

    final Calendar calendar = Calendar.getInstance();
    //    final int year_today = calendar.get(Calendar.YEAR);
    final int month_today = calendar.get(Calendar.MONTH) + 1;//今日所处月份
    final int day_today = calendar.get(Calendar.DAY_OF_MONTH);//今日日期
    private String date = month_today + "#" +day_today;

    private static int DBVersion = 1;//数据库版本

    private static String userID = "18851730833";

    private int breakfast_energy;//早餐能量
    private int lunch_energy;//午餐能量
    private int dinner_energy;//晚餐能量
    private int extra_energy;//加餐能量

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_userName = getActivity().findViewById(R.id.tv_userName);
        tv_stage = getActivity().findViewById(R.id.tv_stage);
        tv_current_KG = getActivity().findViewById(R.id.tv_current_weight);
        tv_BMI = getActivity().findViewById(R.id.tv_BMI);

        /**
         * 从登陆页面获得当前登录用户ID
         * 根据ID获取用户名、体重、身高等信息
         */
        //tv_userName.setText();
        //tv_current_KG.setText(String.valueOf());
        weight = 75;
        height = 178;
        BMI = weight/((height/100)*(height/100));
        //保留两位小数
        DecimalFormat df = new DecimalFormat("#####0.00");
        String str = df.format(BMI);
        tv_BMI.setText(str);

        /**
         * 用户今日摄入能量
         * 添加记录后，应重新加载能量信息
         */
        breakfast_energy = loadFoodInfo(date,"1");
        lunch_energy = loadFoodInfo(date,"2");
        dinner_energy = loadFoodInfo(date,"3");
        extra_energy = loadFoodInfo(date,"4");

    }

    //加载食物能量信息
    public int loadFoodInfo(String date, String temp){
        DBHelper dbHelper = new DBHelper(getActivity(),"HealthApp.db",null,DBVersion);
        int count = 0;
        Cursor cursor1 = dbHelper.search(userID,date,temp);
        while (cursor1.moveToNext()){
            String actID = cursor1.getString(cursor1.getColumnIndex("actID"));
            int num = cursor1.getInt(cursor1.getColumnIndex("engNum"));
            //获取详细信息
            Cursor cursor = dbHelper.actInfo(actID);
            while (cursor.moveToNext()){
                int f_energy = cursor.getInt(cursor.getColumnIndex("foodEnergy"));
                int energy = (int)(num * f_energy);
                count += energy;
            }
        }
        dbHelper.close();
        return count;
    }

}
