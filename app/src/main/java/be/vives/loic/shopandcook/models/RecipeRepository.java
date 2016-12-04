package be.vives.loic.shopandcook.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.vives.loic.shopandcook.models.Recipe;

/**
 * Gather local recipes in a memory-database
 *
 * Created by LOIC on 21/11/2016.
 */
// @TODO : Implement methods fetching data from a REST DB into Recipe
public class RecipeRepository {
    public Map recipes = new HashMap<>();

    public RecipeRepository(Map recipes) {
        this.recipes = recipes;
    }

    public RecipeRepository() {
        List ingredients = new ArrayList();
        ingredients.add(new Ingredient(CategoryIngredient.Cheese, "Emmental"));

        List steps = new ArrayList();
        steps.add("Etape 1 : Lorem ipsum");
        steps.add("Etape 2 : dolor sit");
        steps.add("Etape 3 : amet.");

        recipes.put(1, new Recipe(1, "Fondants au chocolat", ingredients, steps));
        recipes.put(2, new Recipe(2, "Tarte au citron meringuée", ingredients, steps));
        recipes.put(3, new Recipe(3, "Filet mignon en croûte", ingredients, steps));
        recipes.put(4, new Recipe(4, "Original American Cookies", ingredients, steps));
        recipes.put(5, new Recipe(5, "Cheese cake", ingredients, steps));
        recipes.put(6, new Recipe(6, "Lasagnes à la bolognaise", ingredients, steps));
        recipes.put(7, new Recipe(7, "Boeuf Bourguignon", ingredients, steps));
    }

    // FIND
    // ALL
    public List findAll(){
        return new ArrayList(recipes.values());
    }

    // BY ID
    public Recipe findById(int id){
        Recipe r = (Recipe) recipes.get(id);

        if(r == null){
            r = new Recipe(0, "NOT FOUND", null, null);

        }

        System.err.println(r.getTitle());

        return r;
    }

    // BY TITLE
    public Recipe findRecipeByTitle(String recipeTitle){
        return  (Recipe) recipes.get(recipeTitle);
    }

    // update the value of the map
    public void save(){
        // To string the value of the map
    }
}
