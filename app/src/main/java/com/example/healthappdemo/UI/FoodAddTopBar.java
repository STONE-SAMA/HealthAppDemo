package com.example.healthappdemo.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthappdemo.R;

public class FoodAddTopBar extends RelativeLayout {

    private Button btn_back;
    private TextView tv_add_type;
    private TextView tv_add_finish;

    private OnLeftAndRightClickListener listener;

    public void setOnLeftAndRightClickListener(OnLeftAndRightClickListener listener){
        this.listener = listener;
    }

    public interface OnLeftAndRightClickListener{
        void OnLeftClick();
        void OnRightClick();
    }

    public FoodAddTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.food_add_topbar,this);
        btn_back = findViewById(R.id.btn_food_add_left);
        tv_add_type = findViewById(R.id.tv_food_add_type);
        tv_add_finish = findViewById(R.id.tv_food_add_finish);

        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.OnLeftClick();
                }
            }
        });

        tv_add_finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.OnRightClick();
                }
            }
        });

    }


}
