package com.waikato.comp204.javalibrary;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by SadViper on 12/05/2017.
 */

public class MyAdapter extends ArrayAdapter {

    private int titleColor = Color.parseColor("#F8981D");
    private int[] colors = new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#EEEEEF")};
    Doc docs;

    public MyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects, Doc docs) {
        super(context, resource, objects);
        this.docs = docs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        try {

            if (docs.getDocList().get(position).getHomeURL() == null) {
                view.setBackgroundColor(titleColor);
            } else {
                int colorPos = position % colors.length;
                view.setBackgroundColor(colors[colorPos]);
            }
        } catch (Exception ex) {

        }
        return view;
    }
}
