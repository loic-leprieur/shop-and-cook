package be.vives.loic.shopandcook.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.vives.loic.shopandcook.model.Recipe;

/**
 * Gather local recipes in a memory-database
 *
 * Created by LOIC on 21/11/2016.
 */
public class RecipeRepository {
    private Map recipes = new HashMap();

    public RecipeRepository(Map recipes) {
        this.recipes = recipes;
    }

    // GET
    // ALL

    public List findAll(){
        return (List) new ArrayList(recipes.values());
    }

    public Recipe findById(int id){
        return (Recipe) recipes.get(id);
    }

    public Recipe findRecipeByTitle(String recipeTitle){
        return  (Recipe) recipes.get(recipeTitle);
    }

    // POST
}
