package com.mbs.demo.getredbag.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mbs.demo.getredbag.R;
import com.mbs.demo.getredbag.constant.Constant;
import com.mbs.demo.getredbag.recerive.ServiceReceiver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceReceiver.OnPerform {

    private static final String TAG = "MainActivity";
    private PowerManager.WakeLock mWakeLock;
    private TextView mServiceStatus;
    private ServiceReceiver mServiceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView accessibilitySetting = (TextView) findViewById(R.id.accessibility_setting);
        accessibilitySetting.setOnClickListener(this);

        Switch wakeButton = (Switch) findViewById(R.id.wake_lock_toggle);
        wakeButton.setOnClickListener(this);

        TextView sendBroadcast = (TextView) findViewById(R.id.service_send_broadcast_id);
        sendBroadcast.setOnClickListener(this);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "redbag");

        Button powerDownButton = (Button) findViewById(R.id.send_power_down_id);
        powerDownButton.setOnClickListener(this);

        mServiceStatus = (TextView) findViewById(R.id.service_receive_state_id);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACCESSIBILITY_STATUS_RECEIVE);
        mServiceReceiver = new ServiceReceiver();
        mServiceReceiver.setOnPerform(this);
        registerReceiver(mServiceReceiver, intentFilter);

    }

    private void startWakeLock() {
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }

    }

    private void endWakeLock() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }

    }

    @Override
    public void onClick(View v) {
        int type = v.getId();
        switch (type) {
            case R.id.wake_lock_toggle:
                Switch aSwitch = (Switch) v;
                if (aSwitch.isChecked()) {
                    Toast.makeText(this, "wake lock is begin", Toast.LENGTH_SHORT).show();
                    startWakeLock();
                } else {
                    Toast.makeText(this, "wake lock is end", Toast.LENGTH_SHORT).show();
                    endWakeLock();
                }
                break;
            case R.id.accessibility_setting:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;
            case R.id.service_send_broadcast_id:
                mServiceStatus.setText("");
                Intent sendIntent = new Intent(Constant.ACCESSIBILITY_SEND_RECEIVE);
                sendBroadcast(sendIntent);
                break;
            case R.id.send_power_down_id:
                Intent powerDownIntent = new Intent(Constant.ACCESSIBILITY_CONTROL_RECEIVE);
                sendBroadcast(powerDownIntent);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endWakeLock();
        unregisterReceiver(mServiceReceiver);
    }

    @Override
    public void onAction(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onAction: action = " + action);
        if (action.equals(Constant.ACCESSIBILITY_STATUS_RECEIVE)) {

            mServiceStatus.setText("已经启动");


        }
    }
}
