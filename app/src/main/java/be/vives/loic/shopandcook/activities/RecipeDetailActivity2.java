package be.vives.loic.shopandcook.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import be.vives.loic.shopandcook.models.Recipe;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by LOIC on 03/12/2016.
 */
public class RecipeDetailActivity2 extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);

        // get the object Recipe passed by intent from fragment list
        //Recipe mRecipe = (Recipe) getIntent().getExtras().getSerializable("selectedRecipe");

        String recipe_id = getIntent().getExtras().getString("recipe_id");

        /*
        new RecipeDetailActivity2.HttpAsyncTask()
                .execute("http://food2fork.com/api/get?key=d73bc57bb507293fcd95b6c383ce59ca?rId=" + mRecipe.getId());
        */

        new RecipeDetailActivity2.HttpAsyncTask()
                .execute("http://food2fork.com/api/get?key=d73bc57bb507293fcd95b6c383ce59ca&rId="+recipe_id);
    }

    class HttpAsyncTask extends AsyncTask<String, Void, String> {

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
                List ingredients = new ArrayList<>();

                for (int i = 0; i < jsonRecipeIngredients.length(); i++) {
                    ingredients.add(jsonRecipeIngredients.get(i));
                }

            /* set the title of the recipe */
                TextView title = (TextView) findViewById(R.id.recipe_title);
                title.setText(recipe_title);

            /* Add the ingredients to the view */
                TextView ingredientsTextView = (TextView) findViewById(R.id.recipe_ingredients);
                ingredientsTextView.setText(ingredients.toString());

            /* Building the picture of the recipe */
                ImageView img = (ImageView) findViewById(R.id.recipe_picture);

                // Pick the picture with the id of the recipe
                img.setImageBitmap(image);

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
