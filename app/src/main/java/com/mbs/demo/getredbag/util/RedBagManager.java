package com.mbs.demo.getredbag.util;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Path;
import android.os.Build;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.mbs.demo.getredbag.constant.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunmd on 2017/1/4.
 */

public class RedBagManager {

    private static final String TAG = "RedBagManager";

    private static RedBagManager instance;

    private List<AccessibilityNodeInfo> parents = new ArrayList<AccessibilityNodeInfo>();
    private boolean lockStateChangeFlag = false;
    private boolean lockContentChangeFlag = false;
    private AccessibilityNodeInfo mAccessibilityNodeInfo;
    private static AccessibilityService mAccessibilityService;

    static {
        instance = new RedBagManager();

    }

    private RedBagManager() {
        super();
    }

    public static RedBagManager getInstance() {
        return instance;
    }

    public void access(AccessibilityEvent event, int state) {
        access(event, state, null);
    }

    public void access(AccessibilityEvent event, int state, AccessibilityNodeInfo rootInActiveWindow) {
        Log.d(TAG, "access: state is " + state);
        switch (state) {
            case Constant.NOTIFICATION_STATE_CHANGE:
                checkNotification(event);
                break;
            case Constant.WINDOW_CONTENT_CHANGE:
            case Constant.WINDOW_STATE_CHANGE:
                mAccessibilityNodeInfo = rootInActiveWindow;

                Log.d(TAG, "access: lockStateChangeFlag = " + lockStateChangeFlag);
                Log.d(TAG, "access: lockContentChangeFlag = " + lockContentChangeFlag);

                if (lockStateChangeFlag) {
                    lockStateChangeFlag = false;
                } else if (lockContentChangeFlag) {
                    lockContentChangeFlag = false;
                } else {
                    checkChat(event);
                }
                break;
        }

    }

    private void checkChat(AccessibilityEvent event) {

        String className = event.getClassName().toString();
        Log.d(TAG, "checkChat: className = " + className);

        switch (className) {
            case Constant.LAUNCHER_UI:
            case Constant.WINDOW_CHANT:
                getLastPacket();
                break;
            case Constant.LUCKY_MONEY_RECEIVE:
                //inputClick(Constant.CATCH_MONEY_ID, Constant.BAD_DETAIL_ID);
                moneyReceive();
                lockContentChangeFlag = true;
                lockStateChangeFlag = true;
                break;
            case Constant.LUCKY_MONEY_DETAIL:
                inputClick(Constant.EXIT_MONEY_ID);
                lockContentChangeFlag = true;
                lockStateChangeFlag = true;
                break;
        }
    }

    private void moneyReceive() {

        Log.d("moneyReceive", "this is start");

        Path path = new Path();

        path.moveTo(540, 1050);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 450, 50)).build();
        if(mAccessibilityService == null ) {
            Log.e("moneyReceive", "mAccessibilityService is null !");
            return;
        }
        mAccessibilityService.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                Log.d("test", "onCompleted");

                super.onCompleted(gestureDescription);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                Log.d("test", "onCancelled");

                super.onCancelled(gestureDescription);
            }
        }, null);

    }

    private void checkNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts
                    ) {
                String content = text.toString();

                if (content.contains(Constant.CATCH_NOTIFICATION_CONTENT)) {
                    //模拟打开通知栏消息，即打开微信
                    Parcelable data = event.getParcelableData();
                    Log.d(TAG, "checkNotification: data != null is" + (data != null));
                    Log.d(TAG, "checkNotification: data instanceof Notification is " + (data instanceof Notification));
                    if (data != null && data instanceof Notification) {
                        Notification notification = (Notification) data;
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过ID获取控件，并进行模拟点击
     *
     * @param clickId
     */
    private void inputClick(String clickId) {
        inputClick(clickId, null);
    }

    private void inputClick(String firstClickId, String secondClickId) {

        if (mAccessibilityNodeInfo != null) {
            List<AccessibilityNodeInfo> list = mAccessibilityNodeInfo.findAccessibilityNodeInfosByViewId(firstClickId);
            Log.d(TAG, "inputClick: luck money list.isEmpty = " + list.isEmpty());
            if (!list.isEmpty()) {
                for (AccessibilityNodeInfo item : list
                        ) {
                    if (item.isClickable())
                        item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            } else {
                if (secondClickId != null) {
                    list = mAccessibilityNodeInfo.findAccessibilityNodeInfosByViewId(secondClickId);
                    Log.d(TAG, "inputClick: luck money 2222222222 list.isEmpty = " + list.isEmpty());
                    if (!list.isEmpty()) {
                        for (AccessibilityNodeInfo item : list
                                ) {
                            if (item.isClickable())
                                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取List中最后一个红包，并进行模拟点击
     */
    private void getLastPacket() {
        Log.d(TAG, "getLastPacket: beagin");
        recycle(mAccessibilityNodeInfo);
        if (null != parents && parents.size() > 0) {
            parents.get(parents.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }


    /**
     * 回归函数遍历每一个节点，并将含有"领取红包"存进List中
     *
     * @param info
     */
    public void recycle(AccessibilityNodeInfo info) {
        Log.d("recycle", "info = " + info);
        if (info.getChildCount() == 0) {
            if (info.getText() != null) {
                if (Constant.CATCH_CHANT_CONTENT.equals(info.getText().toString())) {
                    if (info.isClickable()) {
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    AccessibilityNodeInfo parent = info.getParent();
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parents.add(parent);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }


    public static void setAccessibilityService(AccessibilityService accessibilityService) {
        mAccessibilityService = accessibilityService;
    }
}
