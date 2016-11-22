package be.vives.loic.shopandcook.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A recipe combines ingredients and steps
 * to prepare a dish
 * Created by LOIC on 21/11/2016.
 */
public class Recipe {
    // unique identifier
    private int id;

    private static int lastId = 0;

    // describe the recipe
    private String title;

    // Required ingredients by Key = ingredient's name
    private Map ingredients = new HashMap();

    // All steps to perform the recipe
    private List steps = new ArrayList();

    /**
     * Construct a recipe with title, ingredients and steps
     * @param title : description
     * @param ingredients : list of required ingredients
     * @param steps : how to perform the recipe by steps
     */
    public Recipe(String title, Map ingredients, List steps) {
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static int getLastId() {
        return lastId;
    }

    public static void setLastId(int lastId) {
        Recipe.lastId = lastId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map ingredients) {
        this.ingredients = ingredients;
    }

    public List getSteps() {
        return steps;
    }

    public void setSteps(List steps) {
        this.steps = steps;
    }
}