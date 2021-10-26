package com.image2pdf.proeditor.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.image2pdf.proeditor.PdfCreater;

public class CollageViewerAdapter extends BaseAdapter {

    PdfCreater mainActivity;

    public  CollageViewerAdapter(PdfCreater creater) {
        mainActivity = creater;
    }

    @Override
    public int getCount() {
        return mainActivity.getDocument().getPageCount();
    }

    @Override
    public Object getItem(int position) {
        return mainActivity.getDocument().getDatasets().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mainActivity.getDocument().getView(position);
    }
}
