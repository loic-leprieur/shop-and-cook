package be.vives.loic.shopandcook.model;

/**
 * all kind of categories
 * for an ingredient
 */
enum CategoryIngredient{
    Meat,
    Cheese,
    Vegetable,
    Fruit,
    Seasoning
}

/**
 * Element composing a Recipe
 * Created by LOIC on 21/11/2016.
 */
public class Ingredient {
    private int id;
    private static int lastId = 0;

    // describe the ingredient
    private String name;

    // from ENUM : Meat, Cheese, Vegetable, Fruit, Seasoning...
    private String category;

    /**
     * default constructor
     * create a new ingredient with a unique ID
     * @param category
     * @param name
     */
    public Ingredient(String category, String name) {
        this.id = lastId++;
        lastId = this.id;
        this.category = category;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
