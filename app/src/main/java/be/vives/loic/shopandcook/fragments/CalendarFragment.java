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
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.activities.RecipeDetailActivity;
import be.vives.loic.shopandcook.models.FavoritesAdapter;
import be.vives.loic.shopandcook.models.Recipe;
import be.vives.loic.shopandcook.models.SwipeToDeleteCallback;
import be.vives.loic.shopandcook.storage.PlannerDao;

public class CalendarFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        PlannerDao plannerDao = new PlannerDao(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.planned_recipes);
        TextView emptyView = view.findViewById(R.id.planned_recipes_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        CalendarView calendarView = view.findViewById(R.id.calendarView2);
        showRecipesForDate(plannerDao, recyclerView, emptyView, formatDate(calendarView.getDate()));

        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) ->
                showRecipesForDate(plannerDao, recyclerView, emptyView, formatDate(year, month, dayOfMonth)));

        return view;
    }

    private void showRecipesForDate(PlannerDao plannerDao, RecyclerView recyclerView, TextView emptyView, String date) {
        List<Recipe> recipes = plannerDao.getRecipesForDate(date);
        emptyView.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(recipes.isEmpty() ? View.GONE : View.VISIBLE);

        FavoritesAdapter adapter = new FavoritesAdapter(recipes, recipe -> {
            Intent i = new Intent(requireActivity().getApplicationContext(), RecipeDetailActivity.class);
            i.putExtra("recipe_id", recipe.getId());
            i.putExtra("recipe_title", recipe.getTitle());
            startActivity(i);
        });
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), position -> {
            Recipe removed = adapter.removeAt(position);
            plannerDao.removeRecipeFromDate(date, removed.getId());
        })).attachToRecyclerView(recyclerView);
    }

    private static String formatDate(long epochMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(epochMillis);
        return formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static String formatDate(int year, int month, int dayOfMonth) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
    }
}
