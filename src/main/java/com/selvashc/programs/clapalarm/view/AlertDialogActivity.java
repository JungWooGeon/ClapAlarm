package com.selvashc.programs.clapalarm.view;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import com.jawon.han.HanActivity;
import com.jawon.han.output.HanDevice;
import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.HanButton;
import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.presenter.AlertPresenter;
import com.selvashc.programs.clapalarm.service.RecVoiceService;

import java.util.Timer;
import java.util.TimerTask;

public class AlertDialogActivity extends HanActivity implements AlertView {

    private HanDevice mHanDevice;
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private AlertPresenter presenter;
    private TimerTask timerTask;
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alertdialog);

        initContextDevice();
        this.presenter = new AlertPresenter(this, mContext);
        initAlert();
        initButton();
    }

    @Override
    protected void onDestroy() {
        if (timerTask != null)
            timer.cancel();
        super.onDestroy();
    }

    private void initContextDevice() {
        mHanDevice = HanApplication.getInstance(this).getHanDevice();
        mContext = HanApplication.getInstance(this);
    }

    private void initAlert() {
        String[] getSharedInfo = presenter.getAlarmTimeAndVolume();
        float volume = (float) 0.1 * Float.parseFloat(getSharedInfo[1]);
        int alarmTime = Integer.parseInt(getSharedInfo[0]);

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mediaPlayer = MediaPlayer.create(this, alert);
        mediaPlayer.setVolume(volume, volume);
        mediaPlayer.start();

        timer = new Timer();
        timerTask = new TimerTask() {
            int count = alarmTime * 60;

            @Override
            public void run() {
                count--;
                if (count == 0) {
                    stopMediaPlayer();
                    cancel();
                    finish();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void initButton() {
        HanButton btnReleaseAlarm = (HanButton) findViewById(R.id.btnReleaseAlarm);
        btnReleaseAlarm.setOnClickListener(v -> {
            stopMediaPlayer();
            finish();
        });

        HanButton btnStopService = (HanButton) findViewById(R.id.btnStopService);
        btnStopService.setOnClickListener(v -> presenter.updateOnOffSetting());
    }

    private void stopMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void stopService() {
        stopMediaPlayer();
        stopService(new Intent(mContext, RecVoiceService.class));
        mHanDevice.displayAndPlayTTS(mContext.getString(R.string.stopService2), true);
        finish();
    }
}
