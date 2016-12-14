package be.vives.loic.shopandcook.models;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import be.vives.loic.shopandcook.R;

/**
 * Created by LOIC on 09/12/2016.
 */

public class RecipeAdapter extends BaseAdapter {

    String[] title;
    String[] img_url;

    public RecipeAdapter(Activity context, String[]title, String[] img_url) {
        this.title = title;
        this.img_url = img_url;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
