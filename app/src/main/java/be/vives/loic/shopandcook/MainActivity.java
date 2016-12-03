package be.vives.loic.shopandcook;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// TODO : From in-memory database to Firebase DB
public class MainActivity extends AppCompatActivity {
    // List view variables
    ListView list;

    // temporary list, will be replaced by a DB of 10 recipe
    // those 10 recipes will be randomly choose and
    // deleted changed at each refreshment of the activity
    String[] recipes_title = {
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
        /*
        mFirebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        */


    }
}
