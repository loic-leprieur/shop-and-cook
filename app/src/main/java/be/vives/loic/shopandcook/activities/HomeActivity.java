package be.vives.loic.shopandcook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import be.vives.loic.shopandcook.R;

public class HomeActivity extends AppCompatActivity {
    Button homebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homebtn = (Button) findViewById(R.id.RecipesButton);
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, RecipesActivity.class);
                startActivity(i);
            }
        });
    }
}
