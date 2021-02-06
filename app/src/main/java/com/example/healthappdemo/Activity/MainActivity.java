package com.example.healthappdemo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.example.healthappdemo.Fragment.HomePageFragment;
import com.example.healthappdemo.R;
import com.example.healthappdemo.Fragment.UserInfoFragment;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //隐藏标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        init();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_homePage:
                        HomePageFragment homePageFragment = new HomePageFragment();
                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                        fragmentTransaction1.replace(R.id.fl_main,homePageFragment);
                        fragmentTransaction1.commit();
                        break;
                    case R.id.rb_user:
                        UserInfoFragment userInfoFragment = new UserInfoFragment();
                        FragmentManager fragmentManager2 = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                        fragmentTransaction2.replace(R.id.fl_main,userInfoFragment);
                        fragmentTransaction2.commit();
                        break;
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MiddleActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_open,0);
            }
        });

    }

    //初始化页面
    private void init(){
        radioGroup = findViewById(R.id.rg_menu);
        imageView = findViewById(R.id.iv_menu);
        HomePageFragment homePageFragment = new HomePageFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_main,homePageFragment);
        fragmentTransaction.commit();
    }
}
