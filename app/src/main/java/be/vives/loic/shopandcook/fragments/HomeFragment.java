package be.vives.loic.shopandcook.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.activities.RecipesActivity;

/**
 * Created by LOIC on 14/01/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private View view;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.RecipesButton).setOnClickListener(this);
        view.findViewById(R.id.MapButton).setOnClickListener(this);
        view.findViewById(R.id.FavoriteButton).setOnClickListener(this);
        view.findViewById(R.id.ShoppingListButton).setOnClickListener(this);
        view.findViewById(R.id.CalendarButton).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        int id = v.getId();
        if (id == R.id.RecipesButton) {
            Intent i = new Intent(getActivity().getApplicationContext(), RecipesActivity.class);
            startActivity(i);
        } else if (id == R.id.FavoriteButton) {
            FavoriteFragment ff = new FavoriteFragment();
            ft.replace(R.id.homeContainer, ff).commit();
        } else if (id == R.id.MapButton) {
            MapFragment mf = new MapFragment();
            ft.replace(R.id.homeContainer, mf).commit();
        } else if (id == R.id.ShoppingListButton) {
            ShoppingFragment sf = new ShoppingFragment();
            ft.replace(R.id.homeContainer, sf).commit();
        } else if (id == R.id.CalendarButton) {
            CalendarFragment cf = new CalendarFragment();
            ft.replace(R.id.homeContainer, cf).commit();
        }
    }
}
