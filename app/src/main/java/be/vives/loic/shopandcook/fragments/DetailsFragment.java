package be.vives.loic.shopandcook.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.activities.RecipesActivity;
import be.vives.loic.shopandcook.models.Recipe;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by LOIC on 03/12/2016.
 */

public class DetailsFragment extends Fragment {

    private View view;
    private RecipesActivity activity;
    private Recipe mRecipe;
    private ArrayList<String> ingredients;
    Boolean isFavorite = false;
    FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);
        activity = (RecipesActivity) getActivity();

        view.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

        fab = (FloatingActionButton) view.findViewById(R.id.addTofavoriteBtn);

        if (isFavorite) {
            fab.setImageResource(R.drawable.ic_favorite);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border);
        }

        view.findViewById(R.id.addTofavoriteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite) {
                    fab.setImageResource(R.drawable.ic_favorite);
                    isFavorite = !isFavorite;
                    addRecipeToFavorites(mRecipe);
                    Toast.makeText(activity.getApplicationContext(), mRecipe.getTitle() + " added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_border);
                    isFavorite = !isFavorite;
                    removeRecipeFromFavorites(mRecipe);
                    Toast.makeText(activity.getApplicationContext(), mRecipe.getTitle() + " removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new LoadSingleRecipe()
                .execute("http://food2fork.com/api/get?key=d73bc57bb507293fcd95b6c383ce59ca&rId=" + mRecipe.getId());
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    private void removeRecipeFromFavorites(Recipe mRecipe) {
        FavoriteFragment.favorites.add(mRecipe);
    }

    private void addRecipeToFavorites(Recipe mRecipe) {
        FavoriteFragment.favorites.remove(mRecipe);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class LoadSingleRecipe extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return GET(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                JSONObject json = new JSONObject(result);
                JSONObject jsonRootObject = json.getJSONObject("recipe");

                String recipe_title = jsonRootObject.getString("title");

                URL url = new URL(jsonRootObject.getString("image_url"));
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                JSONArray jsonRecipeIngredients = jsonRootObject.getJSONArray("ingredients");
                List<String> ingredients = new ArrayList<>();

                for (int i = 0; i < jsonRecipeIngredients.length(); i++) {
                    ingredients.add((String) jsonRecipeIngredients.get(i));
                }

            /* set the title of the recipe */
                activity.getActionBar().setTitle(recipe_title);

            /* Add the ingredients to the view */
                ListView list_ingredients = (ListView) view.findViewById(R.id.recipe_ingredients);
                list_ingredients.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, ingredients));

            /* Building the picture of the recipe */
                ImageView img = (ImageView) view.findViewById(R.id.recipe_picture);

                // Pick the picture with the id of the recipe
                img.setImageBitmap(image);

            /* Remove the progress bar */
                view.findViewById(R.id.progress_bar).setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
