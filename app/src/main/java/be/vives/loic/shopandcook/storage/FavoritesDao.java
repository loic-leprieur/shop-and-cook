package be.vives.loic.shopandcook.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import be.vives.loic.shopandcook.models.Recipe;

public class FavoritesDao {
    private final AppDatabaseHelper dbHelper;

    public FavoritesDao(Context context) {
        dbHelper = AppDatabaseHelper.getInstance(context);
    }

    public void add(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(AppDatabaseHelper.COLUMN_FAVORITE_ID, recipe.getId());
        values.put(AppDatabaseHelper.COLUMN_FAVORITE_TITLE, recipe.getTitle());
        values.put(AppDatabaseHelper.COLUMN_FAVORITE_IMAGE_URL, recipe.getImageUrl());
        dbHelper.getWritableDatabase().insertWithOnConflict(
                AppDatabaseHelper.TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void remove(String recipeId) {
        dbHelper.getWritableDatabase().delete(
                AppDatabaseHelper.TABLE_FAVORITES,
                AppDatabaseHelper.COLUMN_FAVORITE_ID + " = ?",
                new String[]{recipeId});
    }

    public boolean isFavorite(String recipeId) {
        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_FAVORITES,
                new String[]{AppDatabaseHelper.COLUMN_FAVORITE_ID},
                AppDatabaseHelper.COLUMN_FAVORITE_ID + " = ?",
                new String[]{recipeId}, null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    public List<Recipe> getAll() {
        List<Recipe> favorites = new ArrayList<>();
        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_FAVORITES, null, null, null, null, null,
                AppDatabaseHelper.COLUMN_FAVORITE_TITLE + " ASC")) {
            int idIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_FAVORITE_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_FAVORITE_TITLE);
            int imageUrlIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_FAVORITE_IMAGE_URL);
            while (cursor.moveToNext()) {
                Recipe recipe = new Recipe(cursor.getString(idIndex), cursor.getString(titleIndex), null, null);
                recipe.setImageUrl(cursor.getString(imageUrlIndex));
                favorites.add(recipe);
            }
        }
        return favorites;
    }
}
