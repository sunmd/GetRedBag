package com.mbs.demo.getredbag.recerive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mbs.demo.getredbag.constant.Constant;

/**
 * Created by sunmd on 2017/1/6.
 */

public class ServiceReceiver extends BroadcastReceiver {

    private OnPerform mOnPerform;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(Constant.ACCESSIBILITY_CONTROL_RECEIVE.equals(action)) {//关闭service

        }else if (Constant.ACCESSIBILITY_STATUS_RECEIVE.equals(action)) {//接受服务器状态

        } else if (Constant.ACCESSIBILITY_SEND_RECEIVE.equals(action)) {//请求服务器状态查询

        }

        mOnPerform.onAction(intent);
    }

    public void setOnPerform(OnPerform mOnPerform) {
        this.mOnPerform = mOnPerform;
    }

    public interface OnPerform {
        void onAction( Intent intent);
    }
}
