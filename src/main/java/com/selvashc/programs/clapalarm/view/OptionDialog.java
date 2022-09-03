package com.selvashc.programs.clapalarm.view;


import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.jawon.han.output.HanDevice;
import com.jawon.han.widget.HanSpinner;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;
import com.selvashc.programs.clapalarm.R;
import com.selvashc.programs.clapalarm.presenter.OptionPresenter;

import java.util.ArrayList;

public class OptionDialog extends MyHanDialog implements OptionView {

    private OptionPresenter presenter;
    private HanSpinner sensitivitySpinner;
    private HanSpinner timeSpinner;
    private HanSpinner volumeSpinner;

    OptionDialog(Context context, View v, HanDevice hanDevice, Handler updateHandler) {
        super(context, v, hanDevice);
        presenter = new OptionPresenter(this, mContext, updateHandler);
        presenter.readSaveOption();
    }

    @Override
    public void initCombobox(View v) {
        sensitivitySpinner = (HanSpinner) v.findViewById(R.id.sensitivitySpinner);
        timeSpinner = (HanSpinner) v.findViewById(R.id.timeSpinner);
        volumeSpinner = (HanSpinner) v.findViewById(R.id.volumeSpinner);

        ArrayList<String> sItemList = new ArrayList<>(10);
        ArrayList<String> tItemList = new ArrayList<>(10);
        ArrayList<String> vItemList = new ArrayList<>(10);
        for (int i = 1; i < 11; i++) {
            String num = Integer.toString(i);
            sItemList.add(num);
            tItemList.add(num + mContext.getString(R.string.minute));
            vItemList.add(num);
        }

        HanStringArrayAdapter sensitivitySpinnerAdapter = new HanStringArrayAdapter(this.mContext, android.R.layout.simple_spinner_item, sItemList);
        sensitivitySpinner.setAdapter(sensitivitySpinnerAdapter);
        sensitivitySpinner.setOnKeyListener(enterListener);

        HanStringArrayAdapter timeSpinnerAdapter = new HanStringArrayAdapter(this.mContext, android.R.layout.simple_spinner_item, tItemList);
        timeSpinner.setAdapter(timeSpinnerAdapter);
        timeSpinner.setOnKeyListener(enterListener);

        HanStringArrayAdapter volumeSpinnerAdapter = new HanStringArrayAdapter(this.mContext, android.R.layout.simple_spinner_item, vItemList);
        volumeSpinner.setAdapter(volumeSpinnerAdapter);
        volumeSpinner.setOnKeyListener(enterListener);
    }

    @Override
    public void saveOption() {
        hanDevice.displayAndPlayTTS(mContext.getString(R.string.saveOptions), true);
        presenter.addRecent(sensitivitySpinner.getSelectedItem().toString(), Character.toString(timeSpinner.getSelectedItem().toString().charAt(0)), volumeSpinner.getSelectedItem().toString());
    }

    @Override
    public void exit() { super.exit(); }

    @Override
    public void updateOption(int sen, int ala, int vol) {
        sensitivitySpinner.setSelection(sen-1);
        timeSpinner.setSelection(ala-1);
        volumeSpinner.setSelection(vol-1);
    }
}