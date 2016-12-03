package be.vives.loic.shopandcook.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import be.vives.loic.shopandcook.R;

/**
 * Created by LOIC on 03/12/2016.
 */
public class RecipeDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);

        String recipe_title = getIntent().getExtras().getString("selectedRecipe");

        TextView title = (TextView) findViewById(R.id.recipe_title);
        title.setText(recipe_title);
    }
}
