package be.vives.loic.shopandcook.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.models.Recipe;
import be.vives.loic.shopandcook.storage.FavoritesDao;
import be.vives.loic.shopandcook.storage.PlannerDao;
import be.vives.loic.shopandcook.storage.ShoppingListDao;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by LOIC on 03/12/2016.
 * @TODO : Add a 'favorite/like' button then add the current recipe to a list
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private Recipe mRecipe;
    private ArrayList<String> ingredients;
    boolean isFavorite;
    FloatingActionButton fab;

    private FavoritesDao favoritesDao;
    private ShoppingListDao shoppingListDao;
    private PlannerDao plannerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        EdgeToEdge.apply(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        findViewById(R.id.homeFab).setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), HomeActivity.class)));

        favoritesDao = new FavoritesDao(this);
        shoppingListDao = new ShoppingListDao(this);
        plannerDao = new PlannerDao(this);

        String recipe_id = getIntent().getExtras().getString("recipe_id");
        String recipe_title = getIntent().getExtras().getString("recipe_title");
        mRecipe = new Recipe(recipe_id, recipe_title, null, null);
        isFavorite = favoritesDao.isFavorite(recipe_id);

        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

        fab = (FloatingActionButton) findViewById(R.id.addTofavoriteBtn);

        if (isFavorite) {
            fab.setImageResource(R.drawable.ic_favorite);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border);
        }

        findViewById(R.id.addTofavoriteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite) {
                    fab.setImageResource(R.drawable.ic_favorite);
                    isFavorite = !isFavorite;
                    addRecipeToFavorites(mRecipe);
                    Toast.makeText(getApplicationContext(), mRecipe.getTitle() + " added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_border);
                    isFavorite = !isFavorite;
                    removeRecipeFromFavorites(mRecipe);
                    Toast.makeText(getApplicationContext(), mRecipe.getTitle() + " removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.addToPlanningBtn).setOnClickListener(v -> showDatePicker());

        // TheMealDB test API key - see https://www.themealdb.com/api.php
        new LoadSingleRecipe()
                .execute("https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + recipe_id);
    }

    private void addRecipeToFavorites(Recipe r) {
        favoritesDao.add(r);
    }

    private void removeRecipeFromFavorites(Recipe r) {
        favoritesDao.remove(r.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        int id = item.getItemId();
        if (id == R.id.action_home) {
            i = new Intent(getApplicationContext(), HomeActivity.class);
        } else if (id == R.id.action_signout) {
            i = new Intent(getApplicationContext(), be.vives.loic.shopandcook.activities.SignInActivity.class);
        } else if (id == R.id.action_back) {
            this.finish();
        } else if (id == R.id.action_add_to_planning) {
            showDatePicker();
        }
        if (i != null)
            startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        Calendar today = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            boolean saved = plannerDao.addRecipeToDate(date, mRecipe);

            Calendar picked = Calendar.getInstance();
            picked.set(year, month, dayOfMonth);
            String displayDate = DateFormat.getDateInstance(DateFormat.LONG).format(picked.getTime());

            String message = saved
                    ? mRecipe.getTitle() + " added to planning for " + displayDate
                    : "Could not save " + mRecipe.getTitle() + " to planning (recipe_id=" + mRecipe.getId() + ")";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
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
                JSONArray meals = json.optJSONArray("meals");
                if (meals == null || meals.length() == 0) {
                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    return;
                }
                JSONObject jsonRootObject = meals.getJSONObject(0);

                String recipe_title = jsonRootObject.getString("strMeal");
                String thumbUrl = jsonRootObject.getString("strMealThumb");
                mRecipe.setImageUrl(thumbUrl);

                URL url = new URL(thumbUrl);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                List<String> ingredients = new ArrayList<>();
                for (int i = 1; i <= 20; i++) {
                    String ingredient = jsonRootObject.optString("strIngredient" + i, "").trim();
                    if (ingredient.isEmpty()) {
                        continue;
                    }
                    String measure = jsonRootObject.optString("strMeasure" + i, "").trim();
                    ingredients.add(measure.isEmpty() ? ingredient : measure + " " + ingredient);
                }

            /* set the title of the recipe */
                getSupportActionBar().setTitle(recipe_title);

            /* Add the ingredients to the view */
                ListView list_ingredients = (ListView) findViewById(R.id.recipe_ingredients);
                list_ingredients.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, ingredients));
                list_ingredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String ingredient = (String) parent.getItemAtPosition(position);
                        view.setBackgroundColor(ContextCompat.getColor(RecipeDetailActivity.this, R.color.colorAccent));
                        shoppingListDao.add(ingredient);
                    }
                });

            /* Building the picture of the recipe */
                ImageView img = (ImageView) findViewById(R.id.recipe_picture);

                // Pick the picture with the id of the recipe
                img.setImageBitmap(image);

            /* Remove the progress bar */
                findViewById(R.id.progress_bar).setVisibility(View.GONE);

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
