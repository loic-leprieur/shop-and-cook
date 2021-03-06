package be.vives.loic.shopandcook.models;

import java.io.Serializable;

/**
 * all kind of categories
 * for an ingredient
 */
enum CategoryIngredient{
    Meat,
    Cheese,
    Vegetable,
    Fruit,
    Seasoning,
    Baking
}

/**
 * Element composing a Recipe
 * Created by LOIC on 21/11/2016.
 */
public class Ingredient implements Serializable{

    // describe the ingredient
    private String name;

    // from ENUM : Meat, Cheese, Vegetable, Fruit, Seasoning...
    private CategoryIngredient category;

    /**
     * default constructor
     * create a new ingredient with a unique ID
     * @param category
     * @param name
     */
    public Ingredient(int id, CategoryIngredient category, String name) {
        this.category = category;
        this.name = name;
    }

    public Ingredient(String name) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryIngredient getCategory() {
        return category;
    }

    public void setCategory(CategoryIngredient category) {
        this.category = category;
    }
}
