package be.vives.loic.shopandcook.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import be.vives.loic.shopandcook.R;

/**
 * Created by LOIC on 10/01/2017.
 */

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
    private List<Recipe> recipes;
    int resource;
    String response;
    Context context;

    public RecipeListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public RecipeListAdapter(Context context, int resource, List<Recipe> recipes) {
        super(context, resource, recipes);
        this.resource = resource;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout recipesView;
        Recipe recipe = getItem(position);
        if (convertView == null) {
            recipesView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, recipesView, true);
        } else {
            recipesView = (LinearLayout) convertView;
        }

        if (recipe != null) {

            TextView recipeTitle = (TextView) recipesView.findViewById(R.id.recipe_title_row);
            ImageView recipePicture = (ImageView) recipesView.findViewById(R.id.recipe_picture_row);

            recipeTitle.setText(recipe.getTitle());
            recipePicture.setImageBitmap(recipe.getImage());
        }

        return recipesView;
    }
}
