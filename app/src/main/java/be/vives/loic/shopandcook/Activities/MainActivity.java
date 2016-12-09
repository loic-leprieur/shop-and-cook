package be.vives.loic.shopandcook.activities;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import be.vives.loic.shopandcook.R;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static java.security.AccessController.getContext;

// TODO : From in-memory database to Firebase DB for profiles
// TODO : Create a navigationDrawer and a personnalized toolbar
public class MainActivity extends AppCompatActivity {

    // Firebase database fields
    private FirebaseDatabase mFirebaseDB;

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

        //new HttpAsyncTask().execute("http://api.wunderground.com/api/19f29e7113929b18/conditions/q/BE/Kortrijk.json");
        new HttpAsyncTask().execute("https://community-food2fork.p.mashape.com/search?key=d73bc57bb507293fcd95b6c383ce59ca&q=shredded+chicken");

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

                Toast.makeText(getApplicationContext(), "Received!", Toast.LENGTH_LONG).show();

                JSONObject jsonCurrentWeather = json.getJSONObject("current_observation");
                temp = Double.toString(jsonCurrentWeather.getDouble("temp_c"));


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

}


