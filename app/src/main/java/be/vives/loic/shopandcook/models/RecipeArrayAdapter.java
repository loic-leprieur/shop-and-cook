package be.vives.loic.shopandcook.models;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import be.vives.loic.shopandcook.R;

public class RecipeArrayAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] recipe_title;
    private final Integer[] imageId;

    public RecipeArrayAdapter(Activity context, String[] web, Integer[] imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.recipe_title = web;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.recipe_txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.recipe_img);
        txtTitle.setText(recipe_title[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}