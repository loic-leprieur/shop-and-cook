package be.vives.loic.shopandcook.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
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
import be.vives.loic.shopandcook.models.Recipe;
import be.vives.loic.shopandcook.models.RecipeListAdapter;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class RecipesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final static String API_KEY = "d73bc57bb507293fcd95b6c383ce59ca";
    private ArrayList<Recipe> recipes = new ArrayList<>();
    RecipeListAdapter adapter;
    ListView listView;
    SwipeRefreshLayout swipeContainer;
    EditText inputSearch;
    int resultPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage++;
                new HttpAsyncTask()
                        .execute("http://food2fork.com/api/search?key=" + API_KEY + "&page=" + resultPage);
            }
        });

        swipeContainer.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_recipes);
        toolbar.setLogo(R.drawable.ic_search);

        new HttpAsyncTask()
                .execute("http://food2fork.com/api/search?key=" + API_KEY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentSignOut = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intentSignOut);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Recipe r = (Recipe) parent.getItemAtPosition(position);
        String id_recipe = r.getId();

        Intent detailIntent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
        detailIntent.putExtra("recipe_id", id_recipe);

        startActivity(detailIntent);
    }


    class HttpAsyncTask extends AsyncTask<String, Void, String> {

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

            try {
                JSONObject jsonRaw = new JSONObject(result);
                JSONArray jsonRecipesArray = jsonRaw.getJSONArray("recipes");
                JSONObject jsonRecipe = null;
                String id = null;
                String title = null;
                String url = null;
                Recipe recipe = null;
                recipes = new ArrayList<Recipe>();

                for(int i=1; i < 30; i++){
                    jsonRecipe = (JSONObject) jsonRecipesArray.get(i);

                    if(jsonRecipe.has("recipe_id")){
                        id = jsonRecipe.getString("recipe_id");
                        title = jsonRecipe.getString("title");
                        url = jsonRecipe.getString("image_url");
                    }
                    recipe = new Recipe(id, title, null, null);
                    URL imageURL = new URL(url);
                    recipe.setImage(BitmapFactory.decodeStream(imageURL.openConnection().getInputStream()));
                    recipes.add(recipe);
                }

                listView = (ListView) findViewById(R.id.listRecipes);
                adapter = new RecipeListAdapter(getApplicationContext(), R.layout.recipe_row, recipes);
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

            // signal the list is populated
            swipeContainer.setRefreshing(false);
        }
    }

    public static String GET(String url){
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
            if(inputStream != null)
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
