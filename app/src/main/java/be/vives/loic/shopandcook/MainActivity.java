package be.vives.loic.shopandcook;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// TODO : Switch to a Fragment layout design
// TODO : From in-memory database to Firebase DB
public class MainActivity extends AppCompatActivity {
    // List view variables
    ListView list;

    // temporary list, will be replaced by a DB of 10 recipe
    // those 10 recipes will be randomly choose and
    // deleted changed at each refreshment of the activity
    String[] web = {
            "Fondants au chocolat",
            "Tarte au citron meringuée",
            "Filet mignon en croûte",
            "Original American Cookies",
            "Cheese cake",
            "Lasagnes à la bolognaise",
            "Boeuf Bourguignon"
    };

    // pictures will be temporarly stored or accessed
    // by their URL and displayed in the list
    Integer[] imageId = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7
    };

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
        mFirebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // set up the ListView
        list = (ListView) findViewById(R.id.list);
        final RecipeArrayAdapter adapter = new RecipeArrayAdapter(MainActivity.this, web, imageId);
        list.setAdapter(adapter);

        // Show the position and text from an item clicked
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * Click on item handler,
             * will be replaced by an inflated list on trigger.
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
            }
        });
    }
}
