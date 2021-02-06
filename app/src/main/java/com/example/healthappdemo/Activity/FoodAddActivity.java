package com.example.healthappdemo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.healthappdemo.Adapter.FoodAddAdapter;
import com.example.healthappdemo.Adapter.SportAddAdapter;
import com.example.healthappdemo.DBHelper;
import com.example.healthappdemo.UI.FoodAddTopBar;
import com.example.healthappdemo.DataClass.FoodInfo;
import com.example.healthappdemo.R;
import com.example.healthappdemo.DataClass.SportInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加食物、运动记录
 * 传入时间信息、用户账号信息
 * 还需要传入当前登录用户的账号信息
 */

public class FoodAddActivity extends AppCompatActivity {
    private FoodAddTopBar topBar;
    private String type;//顶部导航栏，添加类型
    private TextView tv_type;
    private SearchView searchView;

    private FoodAddAdapter foodAddAdapter;
    private SportAddAdapter sportAddAdapter;
    private Context mContext;

    private ListView listView;

    private static int DBVersion = 1;

    private TextView tv_add_finish;
    private TextView tv_goodsCount;
    private ConstraintLayout mShoppingCartCly;
    private int goodsCount = 0;
    // 贝塞尔曲线中间过程点坐标
    private float[] mCurrentPosition = new float[2];
    // 路径测量
    private PathMeasure mPathMeasure;
    private int flag;
    private ContentValues contentValues = new ContentValues();
    private String userID = "18851730833";
    private String month;
    private String day;
    List<ContentValues> cv_lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_add);

        //隐藏导航栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        topBar = findViewById(R.id.food_add_topBar);
        searchView = findViewById(R.id.sv_food);
        searchView.setIconifiedByDefault(false);

        mShoppingCartCly = findViewById(R.id.cl_layout_food_add);
        tv_goodsCount = findViewById(R.id.tv_goodsCount);
        isShowGoodsCount();//是否显示当前添加数量

        //设定添加类型
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        tv_type = findViewById(R.id.tv_food_add_type);
        tv_type.setText(type);

        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");

        flag = 0;//1早饭、2午饭、3晚饭、4加餐、5为运动
        if(type.equals("添加早餐")){
            flag = 1;
        }else if(type.equals("添加午餐")){
            flag = 2;
        }else if(type.equals("添加晚餐")){
            flag = 3;
        }else if(type.equals("加餐")){
            flag = 4;
        }else if (type.equals("添加运动")){
            searchView.setQueryHint("请输入运动名称");
            flag = 5;
        }

        //搜索组件事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                DBHelper dbHelper = new DBHelper(FoodAddActivity.this,"HealthApp.db",null,DBVersion);
                if(flag == 5){
                    loadSearchResultSport(dbHelper,newText);
                }else{
                    loadSearchResultFood(dbHelper,newText);
                }
                return false;
            }
        });


        tv_add_finish = findViewById(R.id.tv_food_add_finish);

        topBar.setOnLeftAndRightClickListener(new FoodAddTopBar.OnLeftAndRightClickListener() {
            @Override
            public void OnLeftClick() {
                //后退
                finish();
            }

            @Override
            public void OnRightClick() {
                //完成,添加数据到数据库
                DBHelper dbHelper = new DBHelper(FoodAddActivity.this,"HealthApp.db",null,DBVersion);
                for(int i = 0;i < cv_lists.size();i++){
                    ContentValues cv = cv_lists.get(i);
                    dbHelper.energyAdd(cv);
                }
                finish();
            }
        });

        listView = findViewById(R.id.lv_food_info);

        if(flag != 5){
            loadFoodData();
        }else{
            loadSportData();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView iv;
                String str;//食物或是运动的id
                if(flag != 5){//食物
                    iv = (ImageView)view.findViewById(R.id.iv_layout_food_type);
                    TextView tv_food = (TextView)view.findViewById(R.id.tv_food_add_id);
                    str = tv_food.getText().toString();
                }else{//运动
                    iv = (ImageView)view.findViewById(R.id.iv_layout_sport_type);
                    TextView tv_sport = (TextView)view.findViewById(R.id.tv_sport_add_id);
                    str = tv_sport.getText().toString();
                }
                showNumPicker(iv,str);
            }
        });

    }

    //出现数字选择器
    public void showNumPicker(final ImageView iv,final String str){
        final NumPicker np = new NumPicker(FoodAddActivity.this);

        np.show();
        np.setOnComfirmListener(new NumPicker.onComfirmClickListener() {
            @Override
            public void onClick(int num) {
                selectNum(num,iv,str);
                np.dismiss();
            }
        });
        np.setOnCancelListener(new NumPicker.OnCancelClickListener() {
            @Override
            public void onClick() {
                np.dismiss();
            }
        });

    }

    //加载运动搜索的结果
    public void loadSearchResultSport(DBHelper dbHelper,String newText){
        Cursor cursor = dbHelper.searchSport(newText);
        List<SportInfo> sport_list;
        sport_list = new ArrayList<>();
        while(cursor.moveToNext()){
            String sportID = cursor.getString(cursor.getColumnIndex("sportID"));
            String sportName = cursor.getString(cursor.getColumnIndex("sportName"));
            int sportEnergy = cursor.getInt(cursor.getColumnIndex("sportEnergy"));
            //获取数据库中图片
            String sportImg = cursor.getString(cursor.getColumnIndex("sportImg"));
            int imgID = getDrawableResource(sportImg);
            SportInfo sportInfo = new SportInfo(sportID,imgID,sportName,sportEnergy);
            sport_list.add(sportInfo);
        }
        sportAddAdapter = new SportAddAdapter(sport_list,FoodAddActivity.this);
        listView.setAdapter(sportAddAdapter);
    }

    //加载食物搜索的结果
    public void loadSearchResultFood(DBHelper dbHelper,String newText){
        Cursor cursor = dbHelper.searchFood(newText);
        List<FoodInfo> list;
        list = new ArrayList<>();
        while(cursor.moveToNext()) {
            String foodID = cursor.getString(cursor.getColumnIndex("foodID"));
            String foodName = cursor.getString(cursor.getColumnIndex("foodName"));
            int foodEnergy = cursor.getInt(cursor.getColumnIndex("foodEnergy"));
            String foodImg = cursor.getString(cursor.getColumnIndex("foodImg"));
            int imgID = getDrawableResource(foodImg);
            String levelImg = cursor.getString(cursor.getColumnIndex("foodLevelImg"));
            int foodlevelImg = getDrawableResource(levelImg);
            FoodInfo foodInfo = new FoodInfo(foodID,imgID,foodName,foodEnergy,foodlevelImg);
            list.add(foodInfo);
        }
        foodAddAdapter = new FoodAddAdapter(list,FoodAddActivity.this);
        listView.setAdapter(foodAddAdapter);
    }


    //加载食物信息
    public void loadFoodData(){
        List<FoodInfo> list;
        list = new ArrayList<>();

        DBHelper dbHelper1 = new DBHelper(FoodAddActivity.this,"HealthApp.db",null,DBVersion);
        SQLiteDatabase sqLiteDatabase1 = dbHelper1.getReadableDatabase();
        String sql = "select * from food";
        Cursor cursor = sqLiteDatabase1.rawQuery(sql,null);
        while(cursor.moveToNext()) {
            String foodID = cursor.getString(cursor.getColumnIndex("foodID"));
            String foodName = cursor.getString(cursor.getColumnIndex("foodName"));
            int foodEnergy = cursor.getInt(cursor.getColumnIndex("foodEnergy"));
            String foodImg = cursor.getString(cursor.getColumnIndex("foodImg"));
            int imgID = getDrawableResource(foodImg);
            String levelImg = cursor.getString(cursor.getColumnIndex("foodLevelImg"));
            int foodlevelImg = getDrawableResource(levelImg);

            FoodInfo foodInfo = new FoodInfo(foodID,imgID,foodName,foodEnergy,foodlevelImg);
            list.add(foodInfo);
        }

        foodAddAdapter = new FoodAddAdapter(list,this);
        listView.setAdapter(foodAddAdapter);
    }

    //加载运动数据
    public void loadSportData(){
        List<SportInfo> sport_list;
        sport_list = new ArrayList<>();

        DBHelper dbHelper = new DBHelper(FoodAddActivity.this,"HealthApp.db",null,DBVersion);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String sql = "select * from sport";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        while(cursor.moveToNext()){
            String sportID = cursor.getString(cursor.getColumnIndex("sportID"));
            String sportName = cursor.getString(cursor.getColumnIndex("sportName"));
            int sportEnergy = cursor.getInt(cursor.getColumnIndex("sportEnergy"));
            //获取数据库中图片
            String sportImg = cursor.getString(cursor.getColumnIndex("sportImg"));
            int imgID = getDrawableResource(sportImg);

            SportInfo sportInfo = new SportInfo(sportID,imgID,sportName,sportEnergy);
            sport_list.add(sportInfo);
        }

        sportAddAdapter = new SportAddAdapter(sport_list,this);
        listView.setAdapter(sportAddAdapter);
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

    private void selectNum(int num,ImageView iv,String str){
        //num为选择的食物数量或是运动时间
        //iv为当前选中的imageview
        //str为选择的食物或是运动的ID

        /**
         * 数据库添加操作，等待完成按钮触发
         */
        contentValues = new ContentValues();
        contentValues.put("userID",userID);
        String date = month + "#" + day;
        contentValues.put("engDate",date);
        contentValues.put("engType",flag);
        contentValues.put("actID",str);
        contentValues.put("engNum",num + 1);
        cv_lists.add(contentValues);

        addGoodsToCart(iv);
    }

    //判断是否展现添加的数量
    private void isShowGoodsCount(){
        if (goodsCount == 0){
            tv_goodsCount.setVisibility(View.GONE);
        }else{
            tv_goodsCount.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 添加动作的动画效果
     * @param goodsImg
     */
    private void addGoodsToCart(ImageView goodsImg) {
        // 创造出执行动画的主题goodsImg（这个图片就是执行动画的图片,从开始位置出发,经过一个抛物线（贝塞尔曲线）,移动到购物车里）
        final ImageView goods = new ImageView(this);
        goods.setImageDrawable(goodsImg.getDrawable());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(100,100);
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        mShoppingCartCly.addView(goods, params);

        // 得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        int[] parentLocation = new int[2];
        mShoppingCartCly.getLocationInWindow(parentLocation);

        // 得到商品图片的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        goodsImg.getLocationInWindow(startLoc);

        // 得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        tv_add_finish.getLocationInWindow(endLoc);

        // 开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        float startX = startLoc[0] - parentLocation[0] + goodsImg.getWidth() / 2;
        float startY = startLoc[1] - parentLocation[1] + goodsImg.getHeight() / 2;

        // 商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车的1/3
        float toX = endLoc[0] - parentLocation[0] + tv_add_finish.getWidth() / 3;
        float toY = endLoc[1] - parentLocation[1];

        // 开始绘制贝塞尔曲线
        Path path = new Path();
        // 移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        // 使用二阶贝塞尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        // mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，如果是true，path会形成一个闭环
        mPathMeasure = new PathMeasure(path, false);

        // 属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(1000);

        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                // mCurrentPosition此时就是中间距离点的坐标值
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);
            }
        });

        // 开始执行动画
        valueAnimator.start();

        // 动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 购物车商品数量加1
                goodsCount ++;
                isShowGoodsCount();
                tv_goodsCount.setText(String.valueOf(goodsCount));
                // 把执行动画的商品图片从父布局中移除
                mShoppingCartCly.removeView(goods);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
}
