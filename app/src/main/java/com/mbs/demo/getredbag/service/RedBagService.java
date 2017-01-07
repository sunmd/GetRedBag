package com.mbs.demo.getredbag.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.mbs.demo.getredbag.constant.Constant;
import com.mbs.demo.getredbag.recerive.ServiceReceiver;
import com.mbs.demo.getredbag.util.RedBagManager;


public class RedBagService extends AccessibilityService implements ServiceReceiver.OnPerform{

    private static final String TAG = "AccessibilityService";
    private ServiceReceiver mServiceReceiver;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected: service is begin");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACCESSIBILITY_CONTROL_RECEIVE);
        intentFilter.addAction(Constant.ACCESSIBILITY_SEND_RECEIVE);
        mServiceReceiver = new ServiceReceiver();
        mServiceReceiver.setOnPerform(this);
        registerReceiver(mServiceReceiver,intentFilter);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Long startTime = System.currentTimeMillis();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                RedBagManager.getInstance().access(event, Constant.NOTIFICATION_STATE_CHANGE);
                long endTime = System.currentTimeMillis();
                Log.d(TAG, "onAccessibilityEvent: cast time = " + (endTime - startTime));
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                RedBagManager.getInstance().access(event, Constant.WINDOW_CONTENT_CHANGE, getRootInActiveWindow());
                endTime = System.currentTimeMillis();
                Log.d(TAG, "onAccessibilityEvent: cast time = " + (endTime - startTime));
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                RedBagManager.getInstance().access(event, Constant.WINDOW_STATE_CHANGE, getRootInActiveWindow());
                endTime = System.currentTimeMillis();
                Log.d(TAG, "onAccessibilityEvent: cast time = " + (endTime - startTime));
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt: service is interrupt");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onInterrupt: service is onDestroy");
        unregisterReceiver(mServiceReceiver);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onAction(Intent intent) {
        
        String action = intent.getAction();
        Log.d(TAG, "onAction: action = " + action);
        if(action.equals(Constant.ACCESSIBILITY_CONTROL_RECEIVE)) {
            
            super.disableSelf();

            Log.d(TAG, "onAction: do disableSelf");

        } else if (action.equals(Constant.ACCESSIBILITY_SEND_RECEIVE)) {

            Intent statusIntent = new Intent(Constant.ACCESSIBILITY_STATUS_RECEIVE);
            Log.d(TAG, "onAction: " + Constant.ACCESSIBILITY_STATUS_RECEIVE);
            intent.putExtra("flag", 1);
            sendBroadcast(statusIntent);
        }
    }
}

