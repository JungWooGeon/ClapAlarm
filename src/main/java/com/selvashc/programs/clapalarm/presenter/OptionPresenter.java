package com.selvashc.programs.clapalarm.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.view.OptionView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class OptionPresenter {

    private String recentFileName;

    private OptionView view;
    private Context mContext;
    private Handler updateHandler;
    private SharedPreferences sharedPreferences;

    public OptionPresenter(OptionView view, Context mContext, Handler updateHandler) {
        this.view = view;
        this.mContext = mContext;
        this.recentFileName  = mContext.getString(R.string.file_name_recent);
        this.updateHandler = updateHandler;

        this.sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.optionEnglish), Context.MODE_PRIVATE);
    }

    public void addRecent(String sens, String time, String volume) {
        StringBuilder recentResult = new StringBuilder();
        recentResult.append(sens).append(mContext.getString(R.string.slash)).append(time).append(mContext.getString(R.string.slash)).append(volume).append(this.mContext.getString(R.string.next_lign));

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mContext.openFileInput(recentFileName)))) {
            String temp = "";
            int line = 0;
            while ((temp = bufferedReader.readLine()) != null) {
                if (++line > 2)
                    break;
                recentResult.append(temp).append(mContext.getString(R.string.next_lign));
            }
        } catch (IOException e) {
            e.getCause();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.sensitivityEnglish), sens);
        editor.putString(mContext.getString(R.string.alarmTimeEnglish), time);
        editor.putString(mContext.getString(R.string.volumeEnglish), volume);
        editor.apply();

        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(mContext.openFileOutput(recentFileName, Context.MODE_PRIVATE))) {
            oStreamWriter.write(recentResult.toString());
        } catch (IOException e) {
            e.getCause();
        }

        updateHandler.sendMessage(updateHandler.obtainMessage(MainPresenter.UPDATE_RECENT_LIST_VIEW));
        view.exit();
    }

    public void readSaveOption() {
        int sensitivity = Integer.parseInt(sharedPreferences.getString(mContext.getString(R.string.sensitivityEnglish), mContext.getString(R.string.numberOne)));
        int alarmTime = Integer.parseInt(sharedPreferences.getString(mContext.getString(R.string.alarmTimeEnglish), mContext.getString(R.string.numberOne)));
        int volume = Integer.parseInt(sharedPreferences.getString(mContext.getString(R.string.volumeEnglish), mContext.getString(R.string.numberOne)));

        view.updateOption(sensitivity, alarmTime, volume);
    }
}
