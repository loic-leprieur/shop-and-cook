package be.vives.loic.shopandcook.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity {
    Button homebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        EdgeToEdge.apply(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        findViewById(R.id.homeFab).setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), HomeActivity.class)));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.homeContainer, new HomeFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        int id = item.getItemId();
        if (id == R.id.action_signout) {
            i = new Intent(getApplicationContext(), be.vives.loic.shopandcook.activities.SignInActivity.class);
        } else if (id == R.id.action_home) {
            i = new Intent(getApplicationContext(), HomeActivity.class);
        }

        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
