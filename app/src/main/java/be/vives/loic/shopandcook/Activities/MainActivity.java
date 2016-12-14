package be.vives.loic.shopandcook.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import be.vives.loic.shopandcook.R;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

// TODO : From in-memory database to Firebase DB for profiles
// TODO : Create a navigationDrawer and a personnalized toolbar
public class MainActivity extends AppCompatActivity {

    // Firebase database fields
    //private FirebaseDatabase mFirebaseDB;

    private List recipes_title = new ArrayList<>();
    private List recipes_imgURL = new ArrayList<String>(){
        @Override
        public boolean add(String s) {
            boolean isNewValue = true;

            if(this.contains(s)){
               isNewValue = false;
            }
            return isNewValue;
        }
    };
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    /**
     * At activity launch, create a list of items,
     * define the XML layout to use and link
     * the event listeners with the list.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instance of a connection to Firebase
        /*
        mFirebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        */

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        // initialize the api google client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // &q=shredded%20chicken : research with ingredients
        // &sort= sort results
        // &page = give more results
        new HttpAsyncTask()
                .execute("http://food2fork.com/api/search?key=d73bc57bb507293fcd95b6c383ce59ca");
    }

    class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return GET(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            String temp="";
            try {
                JSONObject json = new JSONObject(result);

                JSONArray jsonRecipesSearchResult = json.getJSONArray("recipes");

                JSONObject jsonRecipe = null;

                recipes_title = new ArrayList<String>();

                for(int i=1; i < 30; i++){
                    jsonRecipe = (JSONObject) jsonRecipesSearchResult.get(i);
                    if(jsonRecipe.has("title")){
                        recipes_title.add(jsonRecipe.getString("title"));
                        //recipes_imgURL.add(jsonRecipe.getString("image_url"));
                    }
                }

                ListView list = (ListView) findViewById(R.id.listRecipes);
                list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, recipes_title));

                Toast.makeText(getApplicationContext(),json.getInt("count")+" recipes found", Toast.LENGTH_LONG).show();

            } catch (JSONException e) {

                e.printStackTrace();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
            /* DO EDIT */
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, SignInActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


