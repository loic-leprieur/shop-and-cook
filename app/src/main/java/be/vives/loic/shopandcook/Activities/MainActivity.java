package be.vives.loic.shopandcook.activities;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

import be.vives.loic.shopandcook.R;

// TODO : From in-memory database to Firebase DB
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
    }


}
