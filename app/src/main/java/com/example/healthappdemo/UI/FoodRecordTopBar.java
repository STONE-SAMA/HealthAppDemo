package com.example.healthappdemo.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthappdemo.R;

/**
 * 自定义TopBar
 */
public class FoodRecordTopBar extends RelativeLayout {
    private Button leftbtn;
    private TextView textView;
    private OnLeftButtonClickListener listener;

    public void setOnLeftButtonClickListener(OnLeftButtonClickListener listener){
        this.listener = listener;
    }

    public interface OnLeftButtonClickListener{
        void OnLeftButtonClick();
    }

    public FoodRecordTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.topbar_layout,this);
        leftbtn = findViewById(R.id.btn_left);
        textView = findViewById(R.id.tv_topBar);

        leftbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.OnLeftButtonClick();
                }
            }
        });

    }
}
