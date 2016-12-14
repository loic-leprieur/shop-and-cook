package be.vives.loic.shopandcook.models;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.vives.loic.shopandcook.R;

public class RecipeArrayAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] old_recipe_title;
    private final Integer[] imageId;

    public RecipeArrayAdapter(Activity context, String[] title, Integer[] imageId) {
        super(context, R.layout.list_recipe_row, title);

        this.context = context;
        this.old_recipe_title = title;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_recipe_row, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.list_recipe_txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_recipe_img);
        txtTitle.setText(old_recipe_title[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}