package com.example.healthappdemo.UI;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.healthappdemo.R;

/**
 * 未使用
 */

public class CalendarPop extends PopupWindow {
    private Context mContext;
    private View view;
    private Button btn_return_today;

    public CalendarPop(){
        this.view = LayoutInflater.from(mContext).inflate(R.layout.calendar_pop,null);
        btn_return_today = view.findViewById(R.id.btn_return_today);
        btn_return_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //销毁弹窗
                dismiss();
            }
        });

        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        ColorDrawable dw = new ColorDrawable(0x30000000);
        this.setBackgroundDrawable(dw);

        this.setAnimationStyle(R.style.pop_anim);
    }

}
