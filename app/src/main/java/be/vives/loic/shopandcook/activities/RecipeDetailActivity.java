package be.vives.loic.shopandcook.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.List;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.fragments.FavoriteFragment;
import be.vives.loic.shopandcook.fragments.ShoppingFragment;
import be.vives.loic.shopandcook.models.Recipe;
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
    Boolean isFavorite = false;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String recipe_id = getIntent().getExtras().getString("recipe_id");
        String recipe_title = getIntent().getExtras().getString("recipe_title");
        mRecipe = new Recipe(recipe_id, recipe_title, null, null);

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

        new LoadSingleRecipe()
                .execute("http://food2fork.com/api/get?key=d73bc57bb507293fcd95b6c383ce59ca&rId=" + recipe_id);
    }

    private void addRecipeToFavorites(Recipe r) {
        FavoriteFragment.favorites.add(r);
    }

    private void removeRecipeFromFavorites(Recipe r) {
        FavoriteFragment.favorites.remove(r);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.action_home:
                i = new Intent(getApplicationContext(), HomeActivity.class);
                break;
            case R.id.action_signout:
                i = new Intent(getApplicationContext(), SignInActivity.class);
                break;
            case R.id.action_back:
                this.finish();
        }
        if (i != null)
            startActivity(i);
        return super.onOptionsItemSelected(item);
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
                getSupportActionBar().setTitle(recipe_title);

            /* Add the ingredients to the view */
                ListView list_ingredients = (ListView) findViewById(R.id.recipe_ingredients);
                list_ingredients.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, ingredients));
                list_ingredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String ingredient = (String) parent.getItemAtPosition(position);
                        view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        ShoppingFragment.ingredients.add(ingredient);
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
