package com.selvashc.programs.clapalarm.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.model.AlarmSettings;
import com.selvashc.programs.clapalarm.view.MainView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter {

    static final int UPDATE_RECENT_LIST_VIEW = 1;
    private String bookmarkFileName;
    private String recentFileName;

    private List<AlarmSettings> bookMarkList = new ArrayList<>();
    private List<String> resultBookMarkList = new ArrayList<>();

    private List<AlarmSettings> recentList = new ArrayList<>();
    private List<String> resultRecentList = new ArrayList<>();

    private Context mContext;
    private MainView view;

    private SharedPreferences optionSharedPreferences;

    @SuppressLint("HandlerLeak")
    private Handler updateHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            int message = msg.what;
            if (message == UPDATE_RECENT_LIST_VIEW) {
                readRecent();
            }
        }
    };

    public Handler getUpdateHandler() { return this.updateHandler; }

    public MainPresenter(MainView view, Context mContext) {
        this.view = view;
        this.mContext = mContext;
        this.bookmarkFileName = mContext.getString(R.string.file_name_bookmark);
        this.recentFileName  = mContext.getString(R.string.file_name_recent);

         optionSharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.optionEnglish), Context.MODE_PRIVATE);
    }

    public void readBookMark() {
        bookMarkList.clear();
        resultBookMarkList.clear();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mContext.openFileInput(bookmarkFileName)))) {
            String temp = "";
            while ((temp = bufferedReader.readLine()) != null) {
                String[] tmp = temp.split(mContext.getString(R.string.slash));
                bookMarkList.add(new AlarmSettings(tmp[0], tmp[1], tmp[2]));
                resultBookMarkList.add(mContext.getString(R.string.sensitivity) + mContext.getString(R.string.space) + tmp[0] + mContext.getString(R.string.slash) + mContext.getString(R.string.alarmTime) + mContext.getString(R.string.space) + tmp[1] + mContext.getString(R.string.slash) + mContext.getString(R.string.volume) + mContext.getString(R.string.space) + tmp[2]);
            }
        } catch (IOException e) {
            e.getCause();
        }

        view.showBookMarkList(resultBookMarkList);
    }

    public boolean addBookMark(int position) {

        for (int i = 0; i < bookMarkList.size(); i++) {
            if (recentList.get(position).getVolume().equals(bookMarkList.get(i).getVolume()) &&
                    recentList.get(position).getAlarmTime().equals(bookMarkList.get(i).getAlarmTime()) &&
                    recentList.get(position).getSensitivity().equals(bookMarkList.get(i).getSensitivity())) {
                return false;
            }
        }

        StringBuilder bookMarkResult = new StringBuilder();

        bookMarkResult.append(recentList.get(position).getSensitivity()).append(mContext.getString(R.string.slash)).append(recentList.get(position).getAlarmTime())
                .append(mContext.getString(R.string.slash)).append(recentList.get(position).getVolume()).append(this.mContext.getString(R.string.next_lign));
        for (int i = 0; i < bookMarkList.size(); i++) {
            bookMarkResult.append(bookMarkList.get(i).getSensitivity()).append(mContext.getString(R.string.slash)).append(bookMarkList.get(i).getAlarmTime())
                    .append(mContext.getString(R.string.slash)).append(bookMarkList.get(i).getVolume()).append(this.mContext.getString(R.string.next_lign));
        }

        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(mContext.openFileOutput(bookmarkFileName, Context.MODE_PRIVATE))) {
            oStreamWriter.write(bookMarkResult.toString());
        } catch (IOException e) {
            e.getCause();
        }

        readBookMark();
        return true;
    }

    public void deleteBookMark(int position) {
        StringBuilder bookMarkResult = new StringBuilder();

        bookMarkList.remove(position);
        resultBookMarkList.remove(position);
        for (int i = 0; i < bookMarkList.size(); i++) {
            bookMarkResult.append(bookMarkList.get(i).getSensitivity()).append(mContext.getString(R.string.slash)).append(bookMarkList.get(i).getAlarmTime())
                    .append(mContext.getString(R.string.slash)).append(bookMarkList.get(i).getVolume()).append(mContext.getString(R.string.next_lign));
        }

        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(mContext.openFileOutput(bookmarkFileName, Context.MODE_PRIVATE))) {
            oStreamWriter.write(bookMarkResult.toString());
        } catch (IOException e) {
            e.getCause();
        }

        view.showBookMarkList(resultBookMarkList);
    }

    public void setOptionFromBookMark(int position) {
        setOption(position, bookMarkList);
    }

    private void setOption(int position, List<AlarmSettings> list) {
        SharedPreferences.Editor editor = optionSharedPreferences.edit();
        editor.putString(mContext.getString(R.string.sensitivityEnglish), list.get(position).getSensitivity());
        editor.putString(mContext.getString(R.string.alarmTimeEnglish), list.get(position).getAlarmTime());
        editor.putString(mContext.getString(R.string.volumeEnglish), list.get(position).getVolume());
        editor.apply();
    }

    public void readRecent() {
        recentList.clear();
        resultRecentList.clear();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mContext.openFileInput(recentFileName)))) {
            String temp = "";
            while ((temp = bufferedReader.readLine()) != null) {
                String[] tmp = temp.split(mContext.getString(R.string.slash));
                recentList.add(new AlarmSettings(tmp[0], tmp[1], tmp[2]));
                resultRecentList.add(mContext.getString(R.string.sensitivity) + mContext.getString(R.string.space) + tmp[0] + mContext.getString(R.string.slash) + mContext.getString(R.string.alarmTime) + mContext.getString(R.string.space) + tmp[1] + mContext.getString(R.string.minute) + mContext.getString(R.string.slash) + mContext.getString(R.string.volume) + mContext.getString(R.string.space) + tmp[2]);
            }
        } catch (IOException e) {
            e.getCause();
        }

        view.showRecentList(resultRecentList);
    }

    public void updateRecent(int position) {
        StringBuilder recentResult = new StringBuilder();
        for (int i = 0; i < recentList.size(); i++) {
            if (i == position)
                continue;
            recentResult.append(recentList.get(i).getSensitivity()).append(mContext.getString(R.string.slash)).append(recentList.get(i).getAlarmTime())
                    .append(mContext.getString(R.string.slash)).append(recentList.get(i).getVolume()).append(mContext.getString(R.string.next_lign));
        }

        String positionRecent = recentList.get(position).getSensitivity() + mContext.getString(R.string.slash) + recentList.get(position).getAlarmTime() + mContext.getString(R.string.slash) + recentList.get(position).getVolume() + mContext.getString(R.string.next_lign);
        try (OutputStreamWriter oStreamWriter = new OutputStreamWriter(mContext.openFileOutput(recentFileName, Context.MODE_PRIVATE))) {
            oStreamWriter.write(positionRecent + recentResult.toString());
        } catch (IOException e) {
            e.getCause();
        }

        setOption(position, recentList);
        readRecent();
    }
}
