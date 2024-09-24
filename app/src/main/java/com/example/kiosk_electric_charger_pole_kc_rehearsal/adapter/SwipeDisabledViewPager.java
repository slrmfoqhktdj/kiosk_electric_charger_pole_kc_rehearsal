package com.example.kiosk_electric_charger_pole_kc_rehearsal.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;


public class SwipeDisabledViewPager extends ViewPager {


    public SwipeDisabledViewPager(@NonNull Context context) {
        super(context);
    }

    public SwipeDisabledViewPager(@NonNull Context context, AttributeSet attr) {
        super(context,attr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {



        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
//                Log.d("ViewPager","InterceptTouch Event Action_Down");
                break;
            case MotionEvent.ACTION_UP:
//                Log.d("ViewPager","InterceptTouch Event Action_Down");
                break;
            case MotionEvent.ACTION_CANCEL:
//                Log.d("ViewPager","InterceptTouch Event Action_Down");
                break;
        }


        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("ViewPager","Touch Event");
        return false;
    }

}
