package be.vives.loic.shopandcook.models;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import be.vives.loic.shopandcook.R;

/**
 * Created by LOIC on 10/01/2017.
 */

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
    private final int resource;

    public RecipeListAdapter(Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    public RecipeListAdapter(Context context, int resource, List<Recipe> recipes) {
        super(context, resource, recipes);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        FrameLayout recipeView;
        if (convertView == null) {
            recipeView = (FrameLayout) LayoutInflater.from(getContext()).inflate(resource, parent, false);
        } else {
            recipeView = (FrameLayout) convertView;
        }

        Recipe recipe = getItem(position);
        if (recipe != null) {
            TextView recipeTitle = recipeView.findViewById(R.id.recipe_title_row);
            ImageView recipePicture = recipeView.findViewById(R.id.recipe_picture_row);

            recipeTitle.setText(recipe.getTitle());
            recipePicture.setImageBitmap(recipe.getImage());
        }

        return recipeView;
    }
}
