package be.vives.loic.shopandcook.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
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
import java.net.URL;
import java.util.ArrayList;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.fragments.RecipesFragment;
import be.vives.loic.shopandcook.models.Recipe;
import be.vives.loic.shopandcook.models.RecipeListAdapter;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * @TODO : Search a recipe by name
 */
public class RecipesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    // Authentification string used in API requests
    private final static String API_KEY = "d73bc57bb507293fcd95b6c383ce59ca";

    // array of models used and its adapter in the listview
    private ArrayList<Recipe> mRecipes = new ArrayList<Recipe>();
    private RecipeListAdapter adapter;

    public Recipe selectedRecipe;

    // GUI components
    ListView listView;
    EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.recipesContainer, new RecipesFragment())
                    .commit();
            if (isOnline()) {
                LoadRecipes task = new LoadRecipes();
                task.execute("http://food2fork.com/api/search?key=" + API_KEY);
            } else {
                Toast.makeText(this, "Please connect to retrieve recipes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.action_signout:
                i = new Intent(getApplicationContext(), SignInActivity.class);
                break;
            case R.id.action_home:
                i = new Intent(getApplicationContext(), HomeActivity.class);
                break;
        }
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedRecipe = (Recipe) parent.getItemAtPosition(position);

        Recipe r = (Recipe) parent.getItemAtPosition(position);
        Intent detailIntent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
        detailIntent.putExtra("recipe_id", r.getId());
        detailIntent.putExtra("recipe_title", r.getTitle());

        startActivity(detailIntent);
    }


    private class LoadRecipes extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(RecipesActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait : fetching recipes");
            this.dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            return GET(params[0]);
        }

        @Override
        protected void onPostExecute(final String result) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Consume the Json response
            try {
                JSONObject jsonRaw = new JSONObject(result);
                JSONArray jsonRecipesArray = jsonRaw.getJSONArray("recipes");
                JSONObject jsonRecipe;
                String id = "0";
                String title = "NOT FOUND";
                String url = "NOT FOUND";
                Recipe recipe;
                mRecipes = new ArrayList<Recipe>();

                for (int i = 1; i < 30; i++) {
                    jsonRecipe = (JSONObject) jsonRecipesArray.get(i);

                    if (jsonRecipe.has("recipe_id")) {
                        id = jsonRecipe.getString("recipe_id");
                        title = jsonRecipe.getString("title");
                        url = jsonRecipe.getString("image_url");
                    }
                    recipe = new Recipe(id, title, null, null);
                    URL imageURL = new URL(url);
                    recipe.setImage(BitmapFactory.decodeStream(imageURL.openConnection().getInputStream()));
                    mRecipes.add(recipe);
                }

                // create the view with the data collected
                listView = (ListView) findViewById(R.id.listRecipes);
                adapter = new RecipeListAdapter(getApplicationContext(), R.layout.recipe_row, mRecipes);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(RecipesActivity.this);

                inputSearch = (EditText) findViewById(R.id.inputSearch);
                inputSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                Toast.makeText(getApplicationContext(), jsonRaw.getInt("count") + " recipes found", Toast.LENGTH_LONG).show();

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            // remove the loading message
            if (dialog.isShowing()) {
                dialog.dismiss();
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
                result = "No data!";

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
