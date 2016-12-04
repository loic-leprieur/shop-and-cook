package be.vives.loic.shopandcook.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.activities.MainActivity;
import be.vives.loic.shopandcook.activities.RecipeDetailActivity;

/**
 * A recipe combines ingredients and steps
 * to prepare a dish
 * Created by LOIC on 21/11/2016.
 */
public class Recipe implements Serializable{
    // unique identifier
    private int id;

    private Bitmap image;

    // describe the recipe
    private String title;

    // Required ingredients by Key = ingredient's name
    private List ingredients = new ArrayList();

    // All steps to perform the recipe
    private List steps = new ArrayList();

    /**
     * Construct a recipe with title, ingredients and steps
     * @param title : description
     * @param ingredients : list of required ingredients
     * @param steps : how to perform the recipe by steps
     */
    public Recipe(int id, String title, List ingredients, List steps) {
        this.id = id;
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
    }


    public int getId() {
        return this.id;
    }

    public String getIdStr() {
        return Integer.toString(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List getIngredients() {
        return ingredients;
    }

    public void setIngredients(List ingredients) {
        this.ingredients = ingredients;
    }

    public List getSteps() {
        return steps;
    }

    public void setSteps(List steps) {
        this.steps = steps;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
