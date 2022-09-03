package com.selvashc.programs.clapalarm.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.jawon.han.widget.HanApplication;
import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.thread.AutoVoiceReconizer;
import com.selvashc.programs.clapalarm.view.AlertDialogActivity;

public class RecVoiceService extends Service {

    private AutoVoiceReconizer autoVoiceReconizer;
    private PowerManager powerManager;
    private Context mContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        autoVoiceReconizer = new AutoVoiceReconizer(handler);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mContext = HanApplication.getInstance(this);

        initBroadCastReceiver();
    }

    @Override
    public void onDestroy() {
        autoVoiceReconizer.stopLevelCheck();
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == AutoVoiceReconizer.VOICE_RECORDING_FINISHED) {
                // 화면 깨우기
                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyApp:WAKELOCK");
                wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
                wakeLock.release();

                // 알람 화면 띄우기
                Intent popupIntent = new Intent(getApplicationContext(), AlertDialogActivity.class);
                PendingIntent pie = PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
                try {
                    pie.send();
                } catch (PendingIntent.CanceledException e) {
                    e.getCause();
                }
            }
        }
    };

    private void initBroadCastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        BroadcastReceiver screenOnOff = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(mContext.getString(R.string.screenOff))) {
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.optionEnglish), Context.MODE_PRIVATE);
                    int sensitivity = Integer.parseInt(sharedPreferences.getString(mContext.getString(R.string.sensitivityEnglish), mContext.getString(R.string.numberOne)));

                    autoVoiceReconizer.setSensitivity(sensitivity);
                    autoVoiceReconizer.stopLevelCheck();
                    autoVoiceReconizer.startLevelCheck();
                } else if (intent.getAction().equals(mContext.getString(R.string.screenOn))) {
                    autoVoiceReconizer.stopLevelCheck();
                }
            }
        };

        registerReceiver(screenOnOff, intentFilter);
    }
}
