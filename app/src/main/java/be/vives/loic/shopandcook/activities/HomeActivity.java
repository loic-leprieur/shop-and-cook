package be.vives.loic.shopandcook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
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
        switch (item.getItemId()) {
            case R.id.action_signout:
                i = new Intent(getApplicationContext(), SignInActivity.class);
                break;
            case R.id.action_home:
                i = new Intent(getApplicationContext(), HomeActivity.class);
                break;
        }

        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
