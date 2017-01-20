package be.vives.loic.shopandcook.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by LOIC on 14/01/2017.
 */
public class User implements Serializable {

    private String email;
    private String name;
    private ArrayList<Recipe> favoritesRecipes;
    private ArrayList<Ingredient> shoppingList;

    private Bitmap picture;

    public User(String email, String name, String pictureURL) {
        this.email = email;
        this.name = name;
        try {
            URL url = new URL(pictureURL);
            this.picture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.favoritesRecipes = new ArrayList<Recipe>();
        this.shoppingList = new ArrayList<Ingredient>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public ArrayList<Recipe> getFavoritesRecipes() {
        return favoritesRecipes;
    }

    public void setFavoritesRecipes(ArrayList<Recipe> favoritesRecipes) {
        this.favoritesRecipes = favoritesRecipes;
    }

    public ArrayList<Ingredient> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ArrayList<Ingredient> shoppingList) {
        this.shoppingList = shoppingList;
    }
}
