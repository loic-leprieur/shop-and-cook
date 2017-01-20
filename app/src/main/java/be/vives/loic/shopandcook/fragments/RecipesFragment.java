package be.vives.loic.shopandcook.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.vives.loic.shopandcook.R;

/**
 * Created by LOIC on 12/01/2017.
 */

public class RecipesFragment extends Fragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recipes, container, false);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar_recipes);
        toolbar.setLogo(R.drawable.ic_search);

        return view;
    }
}
