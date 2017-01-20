package be.vives.loic.shopandcook.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import be.vives.loic.shopandcook.R;

/**
 * Created by LOIC on 20/01/2017.
 */

public class ShoppingFragment extends Fragment {
    View view;
    public static ArrayList<String> ingredients = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shopping, container, false);

        ListView listView = (ListView) view.findViewById(R.id.shopping_ingredients);
        listView.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, ingredients));

        return view;
    }
}
