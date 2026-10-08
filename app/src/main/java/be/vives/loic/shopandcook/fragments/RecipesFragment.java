package be.vives.loic.shopandcook.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.activities.RecipesActivity;

public class RecipesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_recipes);
        toolbar.setLogo(R.drawable.ic_search);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Only RecipesActivity can access swipeContainer/inputSearch, since they live in this
        // fragment's inflated view, not the activity's.
        if (getActivity() instanceof RecipesActivity) {
            ((RecipesActivity) getActivity()).onRecipesViewReady(view);
        }
    }
}
