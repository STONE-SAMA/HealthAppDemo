package com.example.healthappdemo.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthappdemo.Adapter.MyListViewAdapter;
import com.example.healthappdemo.DBHelper;
import com.example.healthappdemo.DataClass.EnergyInfo;
import com.example.healthappdemo.UI.FoodRecordTopBar;
import com.example.healthappdemo.R;
import com.example.healthappdemo.UI.CircleProgress;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 用户某日热量记录
 * 还需要传入当前登录用户的账号信息
 */

public class FoodRecordActivity extends AppCompatActivity {
    private static int BUDGERT = 2500;//每日能量预算

    private CircleProgress circleProgress;
//    private Random random;
    private FoodRecordTopBar topBar;
    private Button btn_back_today;
    private TextView textView;//时间

    private ListView breakfastListView;
    private ListView lunchListView;
    private ListView dinnerListView;
    private ListView extraListView;
    private ListView sportListView;

    private TextView tv_breakfast_energy;
    private TextView tv_lunch_energy;
    private TextView tv_dinner_energy;
    private TextView tv_extra_energy;
    private TextView tv_sport_energy;
    private TextView tv_over;
    private TextView tv_sport;

    private RadioGroup radioGroup;

    private MyListViewAdapter listViewAdapter;
    private List<EnergyInfo> lists;

    final Calendar calendar = Calendar.getInstance();
//    final int year_today = calendar.get(Calendar.YEAR);
    final int month_today = calendar.get(Calendar.MONTH) + 1;//今日所处月份
    final int day_today = calendar.get(Calendar.DAY_OF_MONTH);//今日日期

    private CalendarView calendarView;

    private static int DBVersion = 1;//数据库版本
    private String userID = "18851730833";

    private String str;//操作名称
    private int value;//摄入能量值
    private String date;

    private DBHelper dbHelper = new DBHelper(FoodRecordActivity.this,"HealthApp.db",null,DBVersion);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_record);

        //隐藏导航栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        setImgSize();

        topBar = findViewById(R.id.topBar);
        topBar.setOnLeftButtonClickListener(new FoodRecordTopBar.OnLeftButtonClickListener() {
            @Override
            public void OnLeftButtonClick() {
                finish();
            }
        });

        //圆形进度条
        circleProgress = findViewById(R.id.circle_progress_bar);

        /**
         * 从数据库中得到当前时间能量摄入情况，时间只能为2020年
         */
        value = 0;

        tv_breakfast_energy = findViewById(R.id.tv_breakfast_energy);
        tv_lunch_energy = findViewById(R.id.tv_lunch_energy);
        tv_dinner_energy = findViewById(R.id.tv_dinner_energy);
        tv_extra_energy = findViewById(R.id.tv_extra_energy);
        tv_sport_energy = findViewById(R.id.tv_exercise_energy);

        tv_over = findViewById(R.id.tv_over);
        tv_sport = findViewById(R.id.tv_sport);

        textView = findViewById(R.id.tv_topBar);
        //获取当前时间

        //int month = calendar.get(Calendar.MONTH) + 1;
        // day = calendar.get(Calendar.DAY_OF_MONTH);
        textView.setText(month_today + "月" + day_today + "日");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindow();
            }
        });


        breakfastListView = findViewById(R.id.lv_breakfast);
        lunchListView = findViewById(R.id.lv_lunch);
        dinnerListView = findViewById(R.id.lv_dinner);
        extraListView = findViewById(R.id.lv_extra);
        sportListView = findViewById(R.id.lv_sport);

        /**
         * 加载数据库中数据
         */

        //DBHelper dbHelper = new DBHelper(FoodRecordActivity.this,"HealthApp.db",null,DBVersion);

        //获取选择的时间

        String date_choose = textView.getText().toString();
        String month_choose = date_choose.substring(0,date_choose.indexOf("月"));
        String day_choose = date_choose.substring(date_choose.indexOf("月")+1,date_choose.indexOf("日"));
        date = month_choose + "#" + day_choose;

        //加载信息
        load(dbHelper,date);

        //能量计算完成
        circleProgress.setValue(value);

        radioGroup = findViewById(R.id.rg_food_record_menu);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_breakfast_add:
                        RadioButton rb1 = findViewById(R.id.rb_breakfast_add);
                        rb1.setChecked(false);
                        str = "添加早餐";
                        rb_start(str);
                        break;
                    case R.id.rb_lunch_add:
                        RadioButton rb2 = findViewById(R.id.rb_lunch_add);
                        rb2.setChecked(false);
                        str = "添加午餐";
                        rb_start(str);
                        break;
                    case R.id.rb_dinner_add:
                        RadioButton rb3 = findViewById(R.id.rb_dinner_add);
                        rb3.setChecked(false);
                        str = "添加晚餐";
                        rb_start(str);
                        break;
                    case R.id.rb_extraDish_add:
                        RadioButton rb4 = findViewById(R.id.rb_dinner_add);
                        rb4.setChecked(false);
                        str = "加餐";
                        rb_start(str);
                        break;
                    case R.id.rb_sports_add:
                        RadioButton rb5 = findViewById(R.id.rb_sports_add);
                        rb5.setChecked(false);
                        str = "添加运动";
                        rb_start(str);
                        break;
                }
            }
        });

        listviewClick();
    }

    //加载食物信息
    public int loadFoodInfo(DBHelper dbHelper,String date,String temp,ListView listView,int layout){
        int count = 0;
        Cursor cursor1 = dbHelper.search(userID,date,temp);
        RelativeLayout rl = findViewById(layout);
        if(cursor1.getCount() == 0){
            //若是某个listview无数据，隐藏标题
            rl.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }else{
            rl.setVisibility(View.VISIBLE);
            lists = new ArrayList<>();
            while (cursor1.moveToNext()){
                String actID = cursor1.getString(cursor1.getColumnIndex("actID"));
                int num = cursor1.getInt(cursor1.getColumnIndex("engNum"));
                //获取详细信息
                Cursor cursor = dbHelper.actInfo(actID);
                while (cursor.moveToNext()){
                    String f_name = cursor.getString(cursor.getColumnIndex("foodName"));
                    String f_img = cursor.getString(cursor.getColumnIndex("foodImg"));
                    int f_energy = cursor.getInt(cursor.getColumnIndex("foodEnergy"));
                    int energy = (int)(num * f_energy);
                    int imgID = getDrawableResource(f_img);
                    value += energy;
                    count += energy;
                    EnergyInfo energyInfo = new EnergyInfo(actID,imgID,f_name,num,energy);
                    lists.add(energyInfo);
                }
            }
            listViewAdapter = new MyListViewAdapter(lists,this);
            listView.setAdapter(listViewAdapter);
            listView.setVisibility(View.VISIBLE);
        }
        return count;
    }

    //加载运动信息
    public int loadSportInfo(DBHelper dbHelper,String date,String temp,ListView listView,int layout){
        int count = 0;
        Cursor cursor1 = dbHelper.search(userID,date,temp);
        RelativeLayout rl = findViewById(layout);
        if(cursor1.getCount() == 0){
            //若是某个listview无数据，隐藏标题
            rl.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }else{
            rl.setVisibility(View.VISIBLE);
            lists = new ArrayList<>();
            while (cursor1.moveToNext()){
                String actID = cursor1.getString(cursor1.getColumnIndex("actID"));
                int num = cursor1.getInt(cursor1.getColumnIndex("engNum"));
                //获取详细信息
                Cursor cursor = dbHelper.actInfo(actID);
                while (cursor.moveToNext()){
                    String s_name = cursor.getString(cursor.getColumnIndex("sportName"));
                    String s_img = cursor.getString(cursor.getColumnIndex("sportImg"));
                    int s_energy = cursor.getInt(cursor.getColumnIndex("sportEnergy"));
                    int energy = (int)((num * s_energy)/60);
                    count += energy;
                    int imgID = getDrawableResource(s_img);
                    EnergyInfo energyInfo = new EnergyInfo(actID,imgID,s_name,num,energy);
                    lists.add(energyInfo);
                }
            }
            listViewAdapter = new MyListViewAdapter(lists,this);

            listView.setAdapter(listViewAdapter);
            listView.setVisibility(View.VISIBLE);
        }
        return count;
    }


    //获取图片
    public static int  getDrawableResource(String imageName){
        Class drawableClass = R.drawable.class;
        try {
            Field field = drawableClass.getField(imageName);
            int resId = field.getInt(imageName);
            return resId;
        } catch (NoSuchFieldException e) {
            //如果没有在"drawable"下找到imageName,将会返回0
            return 0;
        } catch (IllegalAccessException e) {
            return 0;
        }
    }

    private void showPopwindow(){
//        View parent = ((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this,R.layout.calendar_pop,null);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = 1200;
        final PopupWindow popWindow = new PopupWindow(popView,width,height);
        popWindow.setAnimationStyle(R.style.pop_anim);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popWindow.setBackgroundDrawable(dw);
        //popWindow.showAtLocation(parent, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 80);
        popWindow.showAsDropDown(topBar,0,0);
        actionPopwindow(popView,popWindow);

    }

    //Popwindow点击事件
    private void actionPopwindow(View parent, final PopupWindow popupWindow){
        btn_back_today = parent.findViewById(R.id.btn_return_today);
        btn_back_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(month_today + "月" + day_today + "日");
                popupWindow.dismiss();
                String date_choose = textView.getText().toString();
                String month_choose = date_choose.substring(0,date_choose.indexOf("月"));
                String day_choose = date_choose.substring(date_choose.indexOf("月")+1,date_choose.indexOf("日"));
                date = month_choose + "#" + day_choose;
                load(dbHelper,date);
            }
        });

        calendarView = parent.findViewById(R.id.cv_food);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //获取点击的时间
                int month_select = month + 1;
                textView.setText(month_select +"月" + dayOfMonth + "日");
                popupWindow.dismiss();
                String date_choose = textView.getText().toString();
                String month_choose = date_choose.substring(0,date_choose.indexOf("月"));
                String day_choose = date_choose.substring(date_choose.indexOf("月")+1,date_choose.indexOf("日"));
                date = month_choose + "#" + day_choose;
                load(dbHelper,date);
            }
        });

    }

    //radiobutton启动添加页面
    private void rb_start(String str){
        Intent intent = new Intent(FoodRecordActivity.this, FoodAddActivity.class);
        intent.putExtra("type",str);

        //得到添加的时间
        String date_str = textView.getText().toString();
        String month_str = date_str.substring(0,date_str.indexOf("月"));
        String day_str = date_str.substring(date_str.indexOf("月")+1,date_str.indexOf("日"));

        intent.putExtra("month",month_str);
        intent.putExtra("day",day_str);
        startActivityForResult(intent,233);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        value = 0;
        load(dbHelper,date);
    }

    public void load(DBHelper dbHelper,String date){
        String temp;
        value = 0;
        //判断某日是否有数据
        Cursor cursor = dbHelper.chaeck(userID,date);
        if(cursor.getCount() == 0){
            circleProgress.setValue(0);
        }
        int layout;
        int result;
        //早餐
        temp = "1";
        layout = R.id.rl_breakfast;
        result = loadFoodInfo(dbHelper,date,temp,breakfastListView,layout);
        tv_breakfast_energy.setText(String.valueOf(result));
        //午餐
        temp = "2";
        layout = R.id.rl_lunch;
        result = loadFoodInfo(dbHelper,date,temp,lunchListView,layout);
        tv_lunch_energy.setText(String.valueOf(result));
        //晚餐
        temp = "3";
        layout = R.id.rl_dinner;
        result = loadFoodInfo(dbHelper,date,temp,dinnerListView,layout);
        tv_dinner_energy.setText(String.valueOf(result));
        //加餐
        temp = "4";
        layout = R.id.rl_extra;
        result = loadFoodInfo(dbHelper,date,temp,extraListView,layout);
        tv_extra_energy.setText(String.valueOf(result));
        //运动
        temp = "5";
        layout = R.id.rl_exercise;
        result = loadSportInfo(dbHelper,date,temp,sportListView,layout);
        tv_sport_energy.setText(String.valueOf(result));

        //设定圆环处组件的值
        tv_sport.setText(String.valueOf(result));
        if (value > BUDGERT){
            tv_over.setText(String.valueOf(value-BUDGERT));
        }else{
            tv_over.setText("0");
        }
        circleProgress.setValue(value);
    }

    /**
     * 设定radiobutton上图片大小
     */
    public void setImgSize(){
        RadioButton[] rbs = new RadioButton[5];
        rbs[0] = (RadioButton) findViewById(R.id.rb_breakfast_add);
        rbs[1] = (RadioButton) findViewById(R.id.rb_lunch_add);
        rbs[2] = (RadioButton) findViewById(R.id.rb_dinner_add);
        rbs[3] = (RadioButton) findViewById(R.id.rb_extraDish_add);
        rbs[4] = (RadioButton) findViewById(R.id.rb_sports_add);

        for (RadioButton rb : rbs) {
        //挨着给每个RadioButton加入drawable限制边距以控制显示大小
            Drawable[] drawables = rb.getCompoundDrawables();
        //获取drawables
            Rect r = new Rect(0, 0, drawables[1].getMinimumWidth()*2/5, drawables[1].getMinimumHeight()*2/5);
        //定义一个Rect边界
            drawables[1].setBounds(r);
        //给指定的radiobutton设置drawable边界
        // if (rb.getId() == R.id.rb_more) {
        // r = new Rect(0, 0, drawables[1].getMinimumWidth(), drawables[1].getMinimumHeight());
        // drawables[1].setBounds(r);
        // }
        //添加限制给控件
            rb.setCompoundDrawables(null,drawables[1],null,null);
        }
    }

    //listview点击事件
    private void listviewClick(){
        breakfastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listviewClickAct(view,"1");
            }
        });

        lunchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listviewClickAct(view,"2");
            }
        });

        dinnerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listviewClickAct(view,"3");
            }
        });

        extraListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listviewClickAct(view,"4");
            }
        });

        sportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listviewClickAct(view,"5");
            }
        });

    }

    private void listviewClickAct(final View view, final String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodRecordActivity.this);
        builder.setTitle("提示信息");
        builder.setMessage("确认删除此条记录?");
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView tv = view.findViewById(R.id.tv_energy_record_id);
                String actID = tv.getText().toString();
                dbHelper.deleteFoodRecord(userID,date,actID,type);
                load(dbHelper,date);
                Toast.makeText(FoodRecordActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}
