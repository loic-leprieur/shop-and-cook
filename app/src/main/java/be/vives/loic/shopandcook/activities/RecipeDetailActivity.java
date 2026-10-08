package be.vives.loic.shopandcook.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.CalendarContract;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
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
import be.vives.loic.shopandcook.models.RecipeSteps;
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

    private ArrayList<String> steps = new ArrayList<>();
    private String shareLink;

    private final ActivityResultLauncher<String> notificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> { });

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
                if (!requireSignIn()) {
                    return;
                }
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
        } else if (id == R.id.action_share) {
            shareRecipe();
        } else if (id == R.id.action_cook) {
            startCookingMode();
        }
        if (i != null)
            startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    // README 2.1: guests can browse but not bookmark or plan.
    private boolean requireSignIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return true;
        }
        Toast.makeText(this, R.string.sign_in_required, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void showDatePicker() {
        if (!requireSignIn()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
        Calendar today = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> showTimePicker(year, month, dayOfMonth),
                today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
    }

    // README 3.3: pick the time of the cooking session, used for the reminder and the calendar event.
    private void showTimePicker(int year, int month, int dayOfMonth) {
        new TimePickerDialog(this, (view, hour, minute) -> {
            String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            boolean saved = plannerDao.addRecipeToDate(date, mRecipe);

            Calendar picked = Calendar.getInstance();
            picked.set(year, month, dayOfMonth, hour, minute, 0);
            String displayDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)
                    .format(picked.getTime());

            String message = saved
                    ? mRecipe.getTitle() + " added to planning for " + displayDate
                    : "Could not save " + mRecipe.getTitle() + " to planning (recipe_id=" + mRecipe.getId() + ")";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            if (saved) {
                if (picked.getTimeInMillis() > System.currentTimeMillis()) {
                    ReminderReceiver.schedule(getApplicationContext(), mRecipe.getId(), mRecipe.getTitle(),
                            picked.getTimeInMillis());
                }
                offerCalendarEvent(picked);
            }
        }, 18, 0, true).show();
    }

    // README 3.4: hand the planned session to the user's calendar app (Google Calendar).
    private void offerCalendarEvent(Calendar start) {
        new AlertDialog.Builder(this)
                .setMessage("Add this session to your calendar?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.Events.TITLE, "Cook: " + mRecipe.getTitle())
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.getTimeInMillis())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, start.getTimeInMillis() + 60 * 60 * 1000L);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "No calendar app found", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void shareRecipe() {
        StringBuilder text = new StringBuilder("Check out this recipe on Shop&Cook: ").append(mRecipe.getTitle());
        if (shareLink != null && !shareLink.isEmpty()) {
            text.append('\n').append(shareLink);
        }
        Intent send = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, mRecipe.getTitle())
                .putExtra(Intent.EXTRA_TEXT, text.toString());
        startActivity(Intent.createChooser(send, getString(R.string.share_recipe)));
    }

    private void startCookingMode() {
        if (steps.isEmpty()) {
            Toast.makeText(this, "No instructions available", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, CookingModeActivity.class)
                .putExtra(CookingModeActivity.EXTRA_TITLE, mRecipe.getTitle())
                .putStringArrayListExtra(CookingModeActivity.EXTRA_STEPS, steps));
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

                steps = RecipeSteps.split(jsonRootObject.optString("strInstructions", ""));
                String youtube = jsonRootObject.optString("strYoutube", "");
                shareLink = youtube.isEmpty() ? jsonRootObject.optString("strSource", "") : youtube;

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
