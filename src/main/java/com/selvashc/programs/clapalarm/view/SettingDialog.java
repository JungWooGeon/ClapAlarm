package com.selvashc.programs.clapalarm.view;

import android.content.Context;
import android.view.View;

import com.jawon.han.output.HanDevice;
import com.jawon.han.widget.HanSpinner;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;
import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.presenter.SettingPresenter;

import java.util.ArrayList;

public class SettingDialog extends MyHanDialog implements  SettingView {

    private SettingPresenter presenter;
    private HanSpinner onOffSpinner;

    SettingDialog(Context context, View v, HanDevice hanDevice) {
        super(context, v, hanDevice);
        presenter = new SettingPresenter(this, context);
        presenter.readSaveOption();
    }

    @Override
    public void initCombobox(View v) {
        onOffSpinner = (HanSpinner) v.findViewById(R.id.onOffSpinner);

        ArrayList<String> itemList = new ArrayList<>(10);
        itemList.add(mContext.getString(R.string.off));
        itemList.add(mContext.getString(R.string.on));

        HanStringArrayAdapter onOffSpinnerAdapter = new HanStringArrayAdapter(mContext, android.R.layout.simple_spinner_item, itemList);
        onOffSpinner.setAdapter(onOffSpinnerAdapter);
        onOffSpinner.setOnKeyListener(enterListener);
    }

    @Override
    public void saveOption() {
        hanDevice.displayAndPlayTTS(mContext.getString(R.string.saveSetting), true);
        presenter.setOnOffOption(onOffSpinner.getSelectedItem().toString());
    }

    @Override
    public void exit() { dismiss(); }

    @Override
    public void updateSetting(int position) { onOffSpinner.setSelection(position); }
}