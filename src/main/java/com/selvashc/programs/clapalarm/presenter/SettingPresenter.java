package com.selvashc.programs.clapalarm.presenter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.service.RecVoiceService;
import com.selvashc.programs.clapalarm.view.SettingView;

public class SettingPresenter {

    private SettingView view;
    private Context mContext;
    private SharedPreferences onOffPreferences;
    private Intent serviceIntent;

    public SettingPresenter(SettingView view, Context mContext) {
        this.view = view;
        this.mContext = mContext;
        this.onOffPreferences = mContext.getSharedPreferences(mContext.getString(R.string.optionEnglish), Context.MODE_PRIVATE);
        serviceIntent = new Intent(mContext, RecVoiceService.class);
    }

    public void setOnOffOption(String isOn) {
        if (isOn.equals(mContext.getString(R.string.on)) && !isServiceRunningCheck())
            mContext.startService(serviceIntent);
        else if (isOn.equals(mContext.getString(R.string.off)))
            mContext.stopService(serviceIntent);

        SharedPreferences.Editor editor = onOffPreferences.edit();
        editor.putString(mContext.getString(R.string.onOff), isOn);
        editor.apply();
        view.exit();
    }

    private boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RecVoiceService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void readSaveOption() {
        String onOff = onOffPreferences.getString(mContext.getString(R.string.onOff), mContext.getString(R.string.off));
        if (onOff.equals(mContext.getString(R.string.off))) {
            view.updateSetting(0);
        } else {
            view.updateSetting(1);
        }
    }
}
