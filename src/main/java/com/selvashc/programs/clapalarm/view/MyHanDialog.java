package com.selvashc.programs.clapalarm.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.jawon.han.key.HanBrailleKey;
import com.jawon.han.key.keyboard.usb.USB2Braille;
import com.jawon.han.output.HanDevice;
import com.jawon.han.util.HimsCommonFunc;
import com.jawon.han.widget.HanButton;
import com.jawon.han.widget.HanDialog;
import com.selvashc.programs.clapalarm.R;

public class MyHanDialog extends HanDialog {

    private View v;
    Context mContext;
    HanDevice hanDevice;

    MyHanDialog(Context context, View v, HanDevice hanDevice) {
        super(context);
        this.mContext = context;
        this.v = v;
        this.hanDevice = hanDevice;

        initButton();
        initCombobox(this.v);
    }

    final AdapterView.OnKeyListener enterListener = (v, keyCode, event) -> {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(mContext, event);
        if (event.getAction() == KeyEvent.ACTION_UP && scanCode == HanBrailleKey.HK_ENTER) {
            saveOption();
            return true;
        } else {
            return false;
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(this.getContext(), event);
        /* Call the exit activity */
        if (event.getAction() == KeyEvent.ACTION_UP && HimsCommonFunc.isExitKey(event.getScanCode(), event.getKeyCode())) {
            this.dismiss();
            return true;
        }

        // Call the Cancel
        if (event.getAction() == KeyEvent.ACTION_UP && (scanCode == (HanBrailleKey.HK_ADVANCE4) ||
                (scanCode == (HanBrailleKey.HK_Z | HanBrailleKey.HK_SPACE)))) {
            this.dismiss();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    void initCombobox(View v) {
        // override subclass
    }

    private void initButton() {
        HanButton saveButton = (HanButton) this.v.findViewById(R.id.btnSave);
        saveButton.setOnClickListener(vi -> saveOption());

        HanButton cancelButton = (HanButton) this.v.findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(vi -> dismiss());
    }

    void saveOption() {
        // override subclass
    }

    void exit() { this.dismiss(); }
}
