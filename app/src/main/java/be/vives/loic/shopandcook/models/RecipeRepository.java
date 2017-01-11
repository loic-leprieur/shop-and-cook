package be.vives.loic.shopandcook.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gather local recipes in a memory-database
 *
 * Created by LOIC on 21/11/2016.
 */
// @TODO : Implement methods fetching data from a REST DB into Recipe
public class RecipeRepository {
    public Map recipes = new HashMap<Integer, Recipe>();

    public RecipeRepository() {
    }

    public RecipeRepository(Map recipes) {
        this.recipes = recipes;
    }

    public Map getRecipes() {
        return recipes;
    }

    public void setRecipes(Map recipes) {
        this.recipes = recipes;
    }

    // FIND
    // ALL
    public List findAll(){
        return new ArrayList(recipes.values());
    }

    // BY ID
    public Recipe findById(int id) throws IOException {
        Recipe r = (Recipe) recipes.get(id);

        if(r == null){
            r = new Recipe("0", "NOT FOUND", null, null);
        }

        return r;
    }

    // BY TITLE
    public Recipe findRecipeByTitle(String recipeTitle){
        return  (Recipe) recipes.get(recipeTitle);
    }

    public Bitmap createImageOfRecipe(int id){
        return BitmapFactory.decodeStream(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });
    }

    // update the value of the map
    public void save(){
        // To string the value of the map
    }
}
