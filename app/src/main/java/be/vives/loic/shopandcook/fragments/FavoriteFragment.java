package be.vives.loic.shopandcook.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.activities.RecipeDetailActivity;
import be.vives.loic.shopandcook.models.Recipe;

/**
 * Created by LOIC on 20/01/2017.
 */

public class FavoriteFragment extends Fragment implements AdapterView.OnItemClickListener {
    View view;
    public static ArrayList<Recipe> favorites = new ArrayList<>();
    public ArrayList<String> favoritesStr = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);

        for (Recipe r : favorites) {
            favoritesStr.add(r.getTitle());
        }

        ListView list_ingredients = (ListView) view.findViewById(R.id.recipe_favorites);
        list_ingredients.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, favoritesStr));
        list_ingredients.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String recipeStr = (String) parent.getItemAtPosition(position);
        String recipeId = "";
        Intent i = new Intent(getActivity().getApplicationContext(), RecipeDetailActivity.class);

        for (Recipe r : favorites) {
            if (r.getTitle().contains(recipeStr)) {
                recipeId = r.getId();
            }
        }
        i.putExtra("recipe_id", recipeId);
        i.putExtra("recipe_title", recipeStr);
        startActivity(i);
    }
}
