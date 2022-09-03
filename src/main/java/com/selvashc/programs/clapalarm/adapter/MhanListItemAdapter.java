package com.selvashc.programs.clapalarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jawon.han.widget.adapter.FirstChar;
import com.selvashc.programs.clapalarm.R;

import java.util.List;

public class MhanListItemAdapter extends ArrayAdapter<String> implements FirstChar {
    private Context mContext;
    private int mId;
    private List<String> mItemList;

    public MhanListItemAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mId = resource;
        mItemList = objects;
    }

    @Override
    public String getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mId, null);
            textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }

        final String item = mItemList.get(position);
        String text = item;
        textView.setText(text);

        convertView.setContentDescription(text + mContext.getString(R.string.space) + (position + 1) + mContext.getString(R.string.slash) + getCount());
        return convertView;
    }

    @Override
    public String getFirstChar(int iCurrentPosition) {
        return mItemList.get(iCurrentPosition).substring(mItemList.get(iCurrentPosition).lastIndexOf(mContext.getString(R.string.slash)) + 1).toLowerCase();
    }
}