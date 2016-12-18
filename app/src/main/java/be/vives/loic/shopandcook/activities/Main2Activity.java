package be.vives.loic.shopandcook.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;

import be.vives.loic.shopandcook.R;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<String> recipes_title;
    private ArrayList<String> recipes_imgURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        new Main2Activity.HttpAsyncTask()
                .execute("http://food2fork.com/api/search?key=d73bc57bb507293fcd95b6c383ce59ca");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getApplicationContext(),String.valueOf(view.getId()), Toast.LENGTH_SHORT).show();

        //Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();

        ListView mListView = (ListView) findViewById(R.id.listRecipes);
        TextView txt_recipe = (TextView) mListView.getChildAt(position);
        String id_recipe = (String) txt_recipe.getText();

        Intent detailIntent = new Intent(getApplicationContext(), RecipeDetailActivity2.class);
        detailIntent.putExtra("recipe_id", id_recipe);

        startActivity(detailIntent);
    }


    class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(Main2Activity.this);

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
        protected void onPostExecute(String result) {

            try {
                JSONObject json = new JSONObject(result);
                JSONArray jsonRecipesSearchResult = json.getJSONArray("recipes");
                JSONObject jsonRecipe = null;

                recipes_title = new ArrayList<String>();
                recipes_imgURL = new ArrayList<String>();

                for(int i=1; i < 30; i++){
                    jsonRecipe = (JSONObject) jsonRecipesSearchResult.get(i);
                    if(jsonRecipe.has("recipe_id")){
                        recipes_title.add(jsonRecipe.getString("recipe_id"));
                        recipes_imgURL.add(jsonRecipe.getString("image_url"));
                    }
                }

                ListView list = (ListView) findViewById(R.id.listRecipes);

                list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, recipes_title));
                list.setOnItemClickListener(Main2Activity.this);

                Toast.makeText(getApplicationContext(),json.getInt("count")+" recipes found", Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // remove the loading message
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
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
