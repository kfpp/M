package com.qqonline.calendarcontrol;


import com.qqonline.mpf.R;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 用于生成日历展示的GridView布局
 *
 * @author zhouxin@easier.cn
 */
public class CalendarGridView extends GridView {

    /**
     * 当前操作的上下文对象
     */
    private Context mContext;

    /**
     * CalendarGridView 构造器
     *
     * @param context 当前操作的上下文对象
     */
    public CalendarGridView(Context context) {
        super(context);
        mContext = context;

        setGirdView();
    }

    /**
     * 初始化gridView 控件的布局
     */
    private void setGirdView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        setLayoutParams(params);
        setNumColumns(7);//设置每行列数
        setGravity(Gravity.CENTER_VERTICAL);// 位置居中
        setVerticalSpacing(2);// 垂直间隔
        setHorizontalSpacing(1);// 水平间隔
        setBackgroundColor(getResources().getColor(R.color.calendar_background));
        

        WindowManager windowManager = ((Activity) mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int i = display.getWidth() / 7;
        int j = display.getWidth() - (i * 7);
        int h = display.getHeight();
//        Toast.makeText( ((Activity)mContext), String.valueOf(display.getWidth())+","+String.valueOf(h), Toast.LENGTH_LONG).show();
        int x = j / 2;
        setPadding(x, 0, 0, 0);// 居中
    }
}
