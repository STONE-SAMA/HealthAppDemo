package com.example.healthappdemo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.healthappdemo.DBHelper;
import com.example.healthappdemo.R;

public class MiddleActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private ImageView imageView;
    private DBHelper dbHelper;
    private static int DBVersion = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle);

        //隐藏标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        init();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    //记饮食
                    case R.id.rb_food:
                        Intent intent = new Intent(MiddleActivity.this, FoodRecordActivity.class);
                        startActivity(intent);
                        MiddleActivity.this.finish();
                        break;
                    //记运动
                    case R.id.rb_exercise:
                        Intent intent1 = new Intent(MiddleActivity.this, FoodAddActivity.class);
                        intent1.putExtra("type","添加运动");
                        startActivity(intent1);
                        break;
                    //记体重
                    case R.id.rb_weight:
                        break;
                    //我的计划
                    case R.id.rb_my_plan:
                        /**
                         * @shan
                         */





                        break;
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    @Override
//    public void finish() {
//        super.finish();
//        this.overridePendingTransition(R.anim.activity_close,0);
//    }

    //初始化
    private void init(){
        radioGroup = findViewById(R.id.rg_select_func);
        imageView = findViewById(R.id.iv_close);

    }


}
