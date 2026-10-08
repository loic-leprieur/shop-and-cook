package be.vives.loic.shopandcook.fragments;

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
import be.vives.loic.shopandcook.models.ShoppingListAdapter;
import be.vives.loic.shopandcook.models.SwipeToDeleteCallback;
import be.vives.loic.shopandcook.storage.ShoppingListDao;

public class ShoppingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        ShoppingListDao shoppingListDao = new ShoppingListDao(requireContext());
        List<String> ingredients = shoppingListDao.getAll();

        ShoppingListAdapter adapter = new ShoppingListAdapter(ingredients);

        RecyclerView recyclerView = view.findViewById(R.id.shopping_ingredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), position -> {
            String removed = adapter.removeAt(position);
            shoppingListDao.remove(removed);
        })).attachToRecyclerView(recyclerView);

        return view;
    }
}
