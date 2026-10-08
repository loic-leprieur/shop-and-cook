package be.vives.loic.shopandcook.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.activities.RecipeDetailActivity;
import be.vives.loic.shopandcook.models.FavoritesAdapter;
import be.vives.loic.shopandcook.models.Recipe;
import be.vives.loic.shopandcook.models.SwipeToDeleteCallback;
import be.vives.loic.shopandcook.storage.FavoritesDao;

public class FavoriteFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        FavoritesDao favoritesDao = new FavoritesDao(requireContext());
        List<Recipe> favorites = favoritesDao.getAll();

        FavoritesAdapter adapter = new FavoritesAdapter(favorites, recipe -> {
            Intent i = new Intent(requireActivity().getApplicationContext(), RecipeDetailActivity.class);
            i.putExtra("recipe_id", recipe.getId());
            i.putExtra("recipe_title", recipe.getTitle());
            startActivity(i);
        });

        RecyclerView recyclerView = view.findViewById(R.id.recipe_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), position -> {
            Recipe removed = adapter.removeAt(position);
            favoritesDao.remove(removed.getId());
        })).attachToRecyclerView(recyclerView);

        return view;
    }
}
