package be.vives.loic.shopandcook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by LOIC on 03/12/2016.
 */

public class ListFragment extends android.app.ListFragment implements AdapterView.OnItemClickListener {

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
        // Show the position and text from an item clicked
        Toast.makeText(getActivity(), recipes_title[+position], Toast.LENGTH_SHORT).show();
    }
}
