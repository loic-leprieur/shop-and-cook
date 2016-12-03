package be.vives.loic.shopandcook.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.activities.RecipeDetailActivity;
import be.vives.loic.shopandcook.models.Recipe;
import be.vives.loic.shopandcook.models.RecipeArrayAdapter;
import be.vives.loic.shopandcook.models.RecipeRepository;

/**
 * Created by LOIC on 03/12/2016.
 */

public class ListRecipeFragment extends ListFragment implements AdapterView.OnItemClickListener {

    // temporary list, will be replaced by a DB of 10 recipe
    // those 10 recipes will be randomly choose and
    // deleted changed at each refreshment of the activity
    String[] recipes_title = {
            "Fondants au chocolat",
            "Tarte au citron meringuée",
            "Filet mignon en croûte",
            "Original American Cookies",
            "Cheese cake",
            "Lasagnes à la bolognaise",
            "Boeuf Bourguignon"
    };

    // pictures will be temporarly stored or accessed
    // by their URL and displayed in the list
    Integer[] imageId = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7
    };

    // all local recipes
    public RecipeRepository repo = new RecipeRepository();

    public Recipe selectedRecipe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // define the adapter for the list of recipes (picture + title)
        RecipeArrayAdapter adapter = new RecipeArrayAdapter(getActivity(),
                recipes_title, imageId);
        setListAdapter(adapter);

        // attach the listener to the list
        getListView().setOnItemClickListener(this);
    }

    // on click event handler : change to a new fragment details here
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent detailIntent = new Intent(getContext(), RecipeDetailActivity.class);

        detailIntent.putExtra("selectedRecipe", repo.findById(position+1).getTitle());

        startActivity(detailIntent);
    }
}
