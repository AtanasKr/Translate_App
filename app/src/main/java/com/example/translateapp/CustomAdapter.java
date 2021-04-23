package com.example.translateapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HistoryData> arrayList;
    private TextView langName1, langName2;

    public CustomAdapter(Context context,ArrayList<HistoryData> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.row_item,parent,false);
        langName1 = convertView.findViewById(R.id.langH1);
        langName2 = convertView.findViewById(R.id.langH2);
        langName1.setText(arrayList.get(position).getLangName1());
        langName2.setText(arrayList.get(position).getLangName2());
        return convertView;
    }
}
