package com.selvashc.programs.clapalarm.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.jawon.han.HanActivity;
import com.jawon.han.key.HanBrailleKey;
import com.jawon.han.key.keyboard.usb.USB2Braille;
import com.jawon.han.output.HanDevice;
import com.jawon.han.util.HimsCommonFunc;
import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.HanButton;
import com.jawon.han.widget.HanDialog;
import com.jawon.han.widget.HanListView;
import com.jawon.han.widget.HanMenuPopup;
import com.selvashc.programs.clapalarm.adapter.MhanListItemAdapter;
import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.service.RecVoiceService;
import com.selvashc.programs.clapalarm.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends HanActivity implements HanMenuPopup.OnMenuItemClickListener, HanMenuPopup.OnDismissListener, MainView {
    private MainPresenter presenter;
    private Handler updateHandler;

    private HanDevice mHanDevice;
    private Context mContext;

    boolean mIsShowPopup = false;
    boolean mIsShowPopupCancel = false;
    private HanMenuPopup mHanMenuPopup;

    private List<String> bookMarkList = new ArrayList<>();
    private List<String> recentList = new ArrayList<>();

    private LinearLayout bookmarkListButtonLinearLayout;
    private LinearLayout recentListButtonLinearLayout;

    private HanListView bookMarkListView;
    private HanListView recentListView;
    private MhanListItemAdapter bookMarkListViewAdapter;
    private MhanListItemAdapter recentListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initContextDevice();
        initPresenter();
        initMenuPopup();
        initSharedPreferences();
        initViews();
        initButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isServiceRunningCheck())
            System.exit(0);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(this, event);

        // Menu popup 을 불러올 경우
        if (event.getAction() == KeyEvent.ACTION_UP && (HimsCommonFunc.isMenuKey(event.getKeyCode()) ||
                (scanCode == (HanBrailleKey.HK_M | HanBrailleKey.HK_SPACE)))) {
            if (!mIsShowPopup) {
                mIsShowPopup = true;
                mIsShowPopupCancel = true;
                mHanMenuPopup.show();
            }
            return true;
        }

        // 스페이스-Z을 누를 경우 종료
        if (event.getAction() == KeyEvent.ACTION_UP && (scanCode == (HanBrailleKey.HK_Z | HanBrailleKey.HK_SPACE))) {
            finish();
        }

        // 백스페이스-O를 누를 경우 옵션 대화상자 호출
        if (event.getAction() == KeyEvent.ACTION_UP && scanCode == (HanBrailleKey.HK_O | HanBrailleKey.HK_BACKSPACE)) {
            startDialog(R.layout.layout_option_dlg);
            return true;
        }

        // 백스페이스-F를 누를 경우 켜기/끄기 설정 대화상자 호출
        if (event.getAction() == KeyEvent.ACTION_UP && scanCode == (HanBrailleKey.HK_F | HanBrailleKey.HK_BACKSPACE)) {
            startDialog(R.layout.layout_setting_dlg);
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    private void initContextDevice() {
        mHanDevice = HanApplication.getInstance(this).getHanDevice();
        mContext = HanApplication.getInstance(this);
    }

    private void initPresenter() {
        presenter = new MainPresenter(this, mContext);
        updateHandler = presenter.getUpdateHandler();
    }

    private void initMenuPopup() {
        mHanMenuPopup = new HanMenuPopup(this);
        mHanMenuPopup.setOnDismissListener(this);
        mHanMenuPopup.setOnMenuItemClickListener(this);
        mHanMenuPopup.inflate(R.menu.popup_menu);
    }

    private void initSharedPreferences() {
        SharedPreferences optionShared = getSharedPreferences(mContext.getString(R.string.optionEnglish), MODE_PRIVATE);
        if (!optionShared.contains(mContext.getString(R.string.sensitivityEnglish))) {
            SharedPreferences.Editor editor = optionShared.edit();
            editor.putString(mContext.getString(R.string.sensitivityEnglish), mContext.getString(R.string.numberFive));
            editor.putString(mContext.getString(R.string.alarmTimeEnglish), mContext.getString(R.string.numberOne));
            editor.putString(mContext.getString(R.string.volumeEnglish), mContext.getString(R.string.numberFive));
            editor.apply();
        }
    }

    private void initViews() {
        bookmarkListButtonLinearLayout = (LinearLayout) findViewById(R.id.bookmarkListButtonLinearLayout);
        recentListButtonLinearLayout = (LinearLayout) findViewById(R.id.recentListButtonLinearLayout);

        bookMarkListView = (HanListView) findViewById(R.id.bookmarkListView);
        recentListView = (HanListView) findViewById(R.id.recentListView);

        bookMarkListViewAdapter = new MhanListItemAdapter(this, android.R.layout.simple_list_item_1, bookMarkList);
        bookMarkListView.setAdapter(bookMarkListViewAdapter);
        bookMarkListView.setOnItemClickListener((parent, view, position, id) -> optionSettingsFromBookMarkList(position));
        bookMarkListView.setEmptyView(findViewById(R.id.bookmarkListEmptyView));
        bookMarkListView.setOnKeyListener(bookMarkListOnKeyListener);

        recentListViewAdapter = new MhanListItemAdapter(this, android.R.layout.simple_list_item_1, recentList);
        recentListView.setAdapter(recentListViewAdapter);
        recentListView.setOnItemClickListener((parent, view, position, id) -> optionSettingsFromRecentList(position));
        recentListView.setEmptyView(findViewById(R.id.recentListEmptyView));
        recentListView.setOnKeyListener(recentListOnKeyListener);

        presenter.readBookMark();
        presenter.readRecent();
    }

    private void initButton() {
        HanButton btnSaveFromBookMark = (HanButton) findViewById(R.id.btnSaveFromBookMark);
        HanButton btnDeleteBookMark = (HanButton) findViewById(R.id.btnDeleteBookMark);
        HanButton btnSaveFromRecent = (HanButton) findViewById(R.id.btnSaveFromRecent);
        HanButton btnAddBookMark = (HanButton) findViewById(R.id.btnAddBookMark);

        btnSaveFromBookMark.setOnClickListener(v -> optionSettingsFromBookMarkList(bookMarkListView.getSelectedItemPosition()));

        btnDeleteBookMark.setOnClickListener(v -> deleteBookMark());
        btnDeleteBookMark.setHotKey(HanBrailleKey.HK_D, HanBrailleKey.HK_SPACE);

        btnSaveFromRecent.setOnClickListener(v -> optionSettingsFromRecentList(recentListView.getSelectedItemPosition()));

        btnAddBookMark.setOnClickListener(v -> addBookMark());
        btnAddBookMark.setHotKey(HanBrailleKey.HK_F, HanBrailleKey.HK_SPACE);
    }

    @Override
    public void onDismiss(HanMenuPopup hanMenuPopup) {
        mIsShowPopup = false;
        if (mIsShowPopupCancel)
            mHanDevice.displayAndPlayTTS(mContext.getString(R.string.COMMON_MSG_CANCEL), true);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mIsShowPopupCancel = false;
        switch (item.getItemId()) {
            case R.id.itemSetting:
                startDialog(R.layout.layout_setting_dlg);
                break;
            case R.id.itemOption:
                startDialog(R.layout.layout_option_dlg);
                break;
            case R.id.itemExit:
                finish();
                break;
            default:
                mIsShowPopupCancel = true;
                break;
        }
        return true;
    }

    // 서비스가 실행 중인지 판단
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RecVoiceService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startDialog(int resource) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(resource, null);

        HanDialog dialog = new HanDialog(MainActivity.this);
        if (resource == R.layout.layout_option_dlg) {
            dialog = new OptionDialog(MainActivity.this, v, mHanDevice, updateHandler);
        } else if (resource == R.layout.layout_setting_dlg) {
            dialog = new SettingDialog(MainActivity.this, v, mHanDevice);
        }
        dialog.setContentView(v);
        dialog.setTitle(mContext.getString(R.string.additional));
        dialog.show();
    }

    private void optionSettingsFromRecentList(int position) {
        presenter.updateRecent(position);
        mHanDevice.displayAndPlayTTS(mContext.getString(R.string.completeSettings), true);
        recentListView.requestFocus();
        recentListView.setSelection(0);
    }

    private void optionSettingsFromBookMarkList(int position) {
        presenter.setOptionFromBookMark(position);
        mHanDevice.displayAndPlayTTS(mContext.getString(R.string.completeSettings), true);
        bookMarkListView.requestFocus();
    }

    private void deleteBookMark() {
        presenter.deleteBookMark(bookMarkListView.getSelectedItemPosition());
        mHanDevice.displayAndPlayTTS(mContext.getString(R.string.completeDeleteBookMark), true);
        bookMarkListView.requestFocus();
        bookMarkListView.setSelection(0);
    }

    private void addBookMark() {
        if(presenter.addBookMark(recentListView.getSelectedItemPosition())) {
            mHanDevice.displayAndPlayTTS(mContext.getString(R.string.comleteAddBookMark), true);
            bookMarkListView.requestFocus();
            bookMarkListView.setSelection(0);
        } else {
            mHanDevice.displayAndPlayTTS(mContext.getString(R.string.settingAlreadyExist), true);
        }
    }

    @Override
    public void showBookMarkList(List<String> resultBookMarkList) {
        bookMarkList.clear();
        bookMarkList.addAll(resultBookMarkList);
        bookMarkListViewAdapter.notifyDataSetChanged();

        if (bookMarkList.isEmpty()) {
            bookMarkListView.setVisibility(View.GONE);
            bookmarkListButtonLinearLayout.setVisibility(View.GONE);
        } else {
            bookMarkListView.setVisibility(View.VISIBLE);
            bookmarkListButtonLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showRecentList(List<String> resultRecentList) {
        recentList.clear();
        recentList.addAll(resultRecentList);
        recentListViewAdapter.notifyDataSetChanged();

        if (recentList.isEmpty()) {
            recentListView.setVisibility(View.GONE);
            recentListButtonLinearLayout.setVisibility(View.GONE);
        } else {
            recentListView.setVisibility(View.VISIBLE);
            recentListButtonLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private final AdapterView.OnKeyListener bookMarkListOnKeyListener = (v, keyCode, event) -> {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(mContext, event);
        if (event.getAction() == KeyEvent.ACTION_UP && scanCode == (HanBrailleKey.HK_D | HanBrailleKey.HK_SPACE)) {
            deleteBookMark();
            return true;
        } else {
            return false;
        }
    };

    private final AdapterView.OnKeyListener recentListOnKeyListener = (v, keyCode, event) -> {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(mContext, event);
        if (event.getAction() == KeyEvent.ACTION_UP && scanCode == (HanBrailleKey.HK_F | HanBrailleKey.HK_SPACE)) {
            addBookMark();
            return true;
        } else {
            return false;
        }
    };
}
