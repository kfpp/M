package com.qqonline.calendarcontrol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.LunarUtil;
import com.qqonline.mpf.R;

import android.app.Activity;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class CalendarGridViewAdapter extends BaseAdapter {

	/**
	 * ��ǰ��ʾ������
	 */
    private Calendar calStartDate = Calendar.getInstance();// ��ǰ��ʾ������
    /**
     * ѡ�������
     */
    private Calendar calSelected = Calendar.getInstance(); // ѡ�������

    /**
     * ����ѡ�������
     * @param cal Ҫ���õ�����
     */
    public void setSelectedDate(Calendar cal) {
        calSelected = cal;
    }
    /**
     * ����
     */
    private Calendar calToday = Calendar.getInstance(); // ����
    private int iMonthViewCurrentMonth = 0; // ��ǰ��ͼ��

    // 
    /**
     * ���ݸı�����ڸ�������,
     *��������ؼ���
     */
    private void UpdateStartDateForMonth() {
        calStartDate.set(Calendar.DATE, 1); // ���óɵ��µ�һ��
        iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// �õ���ǰ������ʾ����

        // ����һ��2 ��������1 ���ʣ������
        int iDay = 0;
        int iFirstDayOfWeek = Calendar.MONDAY;
        int iStartDay = iFirstDayOfWeek;
        if (iStartDay == Calendar.MONDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }
        if (iStartDay == Calendar.SUNDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

        calStartDate.add(Calendar.DAY_OF_MONTH, -1);// ���յ�һλ

    }

    ArrayList<java.util.Date> titles;

    private ArrayList<java.util.Date> getDates() {

        UpdateStartDateForMonth();

        ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>();

        for (int i = 1; i <= 42; i++) {
            alArrayList.add(calStartDate.getTime());
            calStartDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        return alArrayList;
    }

    private Activity activity;
    Resources resources;
    private boolean isBigClockActivity;
    // construct
    public CalendarGridViewAdapter(Activity a, Calendar cal,boolean isBigActivity) {
        calStartDate = cal;
        activity = a;
        resources = activity.getResources();
        titles = getDates();
        this.isBigClockActivity=isBigActivity;
    }

    public CalendarGridViewAdapter(Activity a,boolean isBigActivity) {
        activity = a;
        resources = activity.getResources();
        this.isBigClockActivity=isBigActivity;
    }


    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout iv = new LinearLayout(activity);
        iv.setId(position + 5000);
        iv.setGravity(Gravity.CENTER);
        iv.setOrientation(LinearLayout.VERTICAL);
        iv.setBackgroundColor(resources.getColor(R.color.toumin));
       // iv.setDescendantFocusability(focusability);
        LinearLayout.LayoutParams param=new LayoutParams(LayoutParams.WRAP_CONTENT, 450);
        
        Date myDate = (Date) getItem(position);
        Calendar calCalendar = Calendar.getInstance();
        calCalendar.setTime(myDate);

        final int iMonth = calCalendar.get(Calendar.MONTH);
        final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);
        TextView txtToDay = new TextView(activity);// �ձ��ϻ���
        txtToDay.setGravity(Gravity.CENTER);
        TextView txtDay = new TextView(activity);// ����
        txtDay.setGravity(Gravity.CENTER);

        iv.setBackgroundColor(resources.getColor(R.color.toumin));
        if (isBigClockActivity) {
        	txtToDay.setTextSize(DATA.BigCalendarLunarTextSize);
		}
        else {
        	txtToDay.setTextSize(DATA.CalendarLunarTextSize);
		}
        
     //   txtToDay.setTextColor(resources.getColor(R.color.));
        LunarUtil calendarUtil = new LunarUtil(calCalendar);
        if (equalsDate(calToday.getTime(), myDate)) {
            // ��ǰ����
//            iv.setBackgroundColor(resources.getColor(R.color.event_center));
        	iv.setBackgroundDrawable(resources.getDrawable(R.drawable.calendar_daynow_background_corner));
            txtToDay.setText(calendarUtil.GetLunarDay());
        } else {
            txtToDay.setText(calendarUtil.GetLunarDay());
        }

        // ���ñ�����ɫ
        if (equalsDate(calSelected.getTime(), myDate)) {
            // ѡ���
       //     iv.setBackgroundColor(resources.getColor(R.color.selection));
        	iv.setBackgroundDrawable(resources.getDrawable(R.drawable.calendar_daynow_background_corner));
        } else {
            if (equalsDate(calToday.getTime(), myDate)) {
                // ��ǰ����
                iv.setBackgroundColor(resources.getColor(R.color.calendar_zhe_day));
            }
        }
        // ���ñ�����ɫ����

        // ���ڿ�ʼ
        
        if (isBigClockActivity) {
        	txtDay.setTextSize(DATA.BigCalendarTextSize);
		} else {
			txtDay.setTextSize(DATA.CalendarTextSize);
		}
        // �ж��Ƿ��ǵ�ǰ��
        if (iMonth == iMonthViewCurrentMonth) {
            txtToDay.setTextColor(resources.getColor(R.color.ToDayText)); //����ũ��������ɫ
            txtDay.setTextColor(resources.getColor(R.color.Text));			//���¹���������ɫ
        } else {
            txtDay.setTextColor(resources.getColor(R.color.noMonth));    //�Ǳ��¹���������ɫ
            txtToDay.setTextColor(resources.getColor(R.color.noMonth));	//�Ǳ���ũ��������ɫ
        }
        // �ж���������
       
        if (iDay == 7) {
            // ����
        	if (iMonth == iMonthViewCurrentMonth) {
        		txtDay.setTextColor(resources.getColor(R.color.text_6));    
                txtToDay.setTextColor(resources.getColor(R.color.text_6));	
        	}
        	
        } else if (iDay == 1) {
            // ����
         //   iv.setBackgroundColor(resources.getColor(R.color.text_7));
        	if (iMonth == iMonthViewCurrentMonth) {
        		txtDay.setTextColor(resources.getColor(R.color.text_7));    
                txtToDay.setTextColor(resources.getColor(R.color.text_7));	
			}
        	
        } else {

        }
        // �ж��������ս���
        int day = myDate.getDate(); // ����
        txtDay.setText(String.valueOf(day));
        txtDay.setId(position + 500);
        iv.setTag(myDate);
        LinearLayout.LayoutParams lp;
        if (isBigClockActivity) {
        	lp = new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, 35);
		}
        else {
        	lp = new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		}
        
        iv.addView(txtDay, lp);

        LinearLayout.LayoutParams lp1 =null;
        if (isBigClockActivity) {
        	lp1 = new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, 40);
		} else {
			lp1 = new LinearLayout.LayoutParams(
	                LayoutParams.FILL_PARENT, 32);
		}
        
        iv.addView(txtToDay, lp1);
        return iv;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private Boolean equalsDate(Date date1, Date date2) {

        if (date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDate() == date2.getDate()) {
            return true;
        } else {
            return false;
        }

    }

}
