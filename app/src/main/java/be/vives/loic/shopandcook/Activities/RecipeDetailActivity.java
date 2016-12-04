package be.vives.loic.shopandcook.activities;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.models.Recipe;

/**
 * Created by LOIC on 03/12/2016.
 */
public class RecipeDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);

        // get the object Recipe passed by intent from fragment list
        Recipe mRecipe = (Recipe) getIntent().getExtras().getSerializable("selectedRecipe");

        /* set the title of the recipe */
        TextView title = (TextView) findViewById(R.id.recipe_title);
        title.setText(mRecipe.getTitle());

        /* Building the picture of the recipe */
        ImageView img = (ImageView) findViewById(R.id.recipe_picture);

        // Pick the picture with the id of the recipe
        int resdID = getResources().getIdentifier("image"+mRecipe.getId(), "drawable", getPackageName());
        img.setImageResource(resdID);
    }
}
