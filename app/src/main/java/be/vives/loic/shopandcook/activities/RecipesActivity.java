package be.vives.loic.shopandcook.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.fragments.RecipesFragment;
import be.vives.loic.shopandcook.models.Recipe;
import be.vives.loic.shopandcook.models.RecipeListAdapter;

public class RecipesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "RecipesActivity";

    // TheMealDB test API key - see https://www.themealdb.com/api.php
    private final static String API_KEY = "1";

    private static final long SEARCH_DEBOUNCE_MS = 400;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler searchHandler = new Handler(Looper.getMainLooper());

    // In-memory cache of the default browse listing, shared across activity instances so
    // navigating back to this screen doesn't refetch it; only pull-to-refresh forces a reload.
    private static ArrayList<Recipe> cachedRecipes;

    private EditText inputSearch;
    private SwipeRefreshLayout swipeContainer;

    public Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        EdgeToEdge.apply(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        findViewById(R.id.homeFab).setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), HomeActivity.class)));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipesContainer, new RecipesFragment())
                    .commit();
        }
    }

    // Called by RecipesFragment once its view is created, since swipeContainer/inputSearch
    // live inside that fragment's layout rather than the activity's own.
    public void onRecipesViewReady(View fragmentView) {
        swipeContainer = fragmentView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this::refresh);

        inputSearch = fragmentView.findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(() -> search(query), SEARCH_DEBOUNCE_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (isOnline()) {
            loadInitialRecipes();
        } else {
            Toast.makeText(this, "Please connect to retrieve recipes", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        int id = item.getItemId();
        if (id == R.id.action_signout) {
            i = new Intent(getApplicationContext(), be.vives.loic.shopandcook.activities.SignInActivity.class);
        } else if (id == R.id.action_home) {
            i = new Intent(getApplicationContext(), HomeActivity.class);
        }
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedRecipe = (Recipe) parent.getItemAtPosition(position);

        Recipe r = (Recipe) parent.getItemAtPosition(position);
        Intent detailIntent = new Intent(getApplicationContext(), be.vives.loic.shopandcook.activities.RecipeDetailActivity.class);
        detailIntent.putExtra("recipe_id", r.getId());
        detailIntent.putExtra("recipe_title", r.getTitle());

        startActivity(detailIntent);
    }

    // Loads the default browse listing once per app session; later visits to this screen
    // reuse the cache instead of hitting the network again.
    private void loadInitialRecipes() {
        if (cachedRecipes != null) {
            showRecipes(cachedRecipes);
            return;
        }

        swipeContainer.setRefreshing(true);
        fetchRecipes(buildUrl(""), "", () -> swipeContainer.setRefreshing(false));
    }

    // Searches recipes by name; an empty query reuses the cached browse listing if available.
    private void search(String query) {
        if (query.isEmpty() && cachedRecipes != null) {
            showRecipes(cachedRecipes);
            return;
        }
        fetchRecipes(buildUrl(query), query, null);
    }

    // Pull-to-refresh: always hits the network, refreshing the cache when browsing (empty query).
    private void refresh() {
        String query = inputSearch != null ? inputSearch.getText().toString().trim() : "";
        fetchRecipes(buildUrl(query), query, () -> {
            if (swipeContainer != null) {
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private String buildUrl(String query) {
        return query.isEmpty()
                ? "https://www.themealdb.com/api/json/v1/" + API_KEY + "/search.php?f=a"
                : "https://www.themealdb.com/api/json/v1/" + API_KEY + "/search.php?s=" + Uri.encode(query);
    }

    private void fetchRecipes(String url, String query, Runnable onDone) {
        executorService.execute(() -> {
            String response = GET(url);
            ArrayList<Recipe> recipes = parseRecipes(response);
            if (query.isEmpty()) {
                cachedRecipes = recipes;
            }
            runOnUiThread(() -> {
                showRecipes(recipes);
                if (onDone != null) {
                    onDone.run();
                }
            });
        });
    }

    // Consume the JSON response. Runs off the main thread since it downloads each recipe's image.
    private ArrayList<Recipe> parseRecipes(String response) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            JSONObject jsonRaw = new JSONObject(response);
            JSONArray jsonMeals = jsonRaw.optJSONArray("meals");
            if (jsonMeals == null) {
                return recipes;
            }

            for (int i = 0; i < jsonMeals.length(); i++) {
                JSONObject jsonMeal = jsonMeals.getJSONObject(i);
                Recipe recipe = new Recipe(jsonMeal.getString("idMeal"), jsonMeal.getString("strMeal"), null, null);
                String thumbUrl = jsonMeal.getString("strMealThumb");
                recipe.setImageUrl(thumbUrl);
                URL imageURL = new URL(thumbUrl);
                recipe.setImage(BitmapFactory.decodeStream(imageURL.openConnection().getInputStream()));
                recipes.add(recipe);
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Failed to parse recipes response", e);
        }
        return recipes;
    }

    private void showRecipes(ArrayList<Recipe> recipes) {
        // create the view with the data collected
        ListView listView = findViewById(R.id.listRecipes);
        RecipeListAdapter adapter = new RecipeListAdapter(getApplicationContext(), R.layout.recipe_row, recipes);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        Toast.makeText(getApplicationContext(), recipes.size() + " recipes found", Toast.LENGTH_LONG).show();
    }

    public static String GET(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream()) {
                return convertInputStreamToString(inputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch recipes from " + url, e);
            return "No data!";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}
