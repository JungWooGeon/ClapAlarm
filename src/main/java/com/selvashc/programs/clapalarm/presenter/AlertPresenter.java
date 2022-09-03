package com.selvashc.programs.clapalarm.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.view.AlertView;

public class AlertPresenter {

    private AlertView view;
    private Context mContext;
    private SharedPreferences sharedPreferences;

    public AlertPresenter(AlertView view, Context mContext) {
        this.view = view;
        this.mContext = mContext;

        sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.optionEnglish), Context.MODE_PRIVATE);
    }

    public void updateOnOffSetting() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.onOff), mContext.getString(R.string.off));
        editor.apply();

        view.stopService();
    }

    public String[] getAlarmTimeAndVolume() {
        return new String[] {sharedPreferences.getString(mContext.getString(R.string.alarmTimeEnglish), "1"),
                            sharedPreferences.getString(mContext.getString(R.string.volumeEnglish), "1")};
    }
}
