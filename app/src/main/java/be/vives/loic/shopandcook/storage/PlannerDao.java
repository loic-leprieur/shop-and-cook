package be.vives.loic.shopandcook.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import be.vives.loic.shopandcook.models.Recipe;

/**
 * Recipes scheduled on a given date (format "yyyy-MM-dd"), for the Planning screen.
 */
public class PlannerDao {
    private final AppDatabaseHelper dbHelper;

    public PlannerDao(Context context) {
        dbHelper = AppDatabaseHelper.getInstance(context);
    }

    /**
     * @return true if the recipe was actually persisted for that date.
     */
    public boolean addRecipeToDate(String date, Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(AppDatabaseHelper.COLUMN_PLANNER_DATE, date);
        values.put(AppDatabaseHelper.COLUMN_PLANNER_RECIPE_ID, recipe.getId());
        values.put(AppDatabaseHelper.COLUMN_PLANNER_TITLE, recipe.getTitle());
        values.put(AppDatabaseHelper.COLUMN_PLANNER_IMAGE_URL, recipe.getImageUrl());
        long rowId = dbHelper.getWritableDatabase().insertWithOnConflict(
                AppDatabaseHelper.TABLE_PLANNER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return rowId != -1;
    }

    public void removeRecipeFromDate(String date, String recipeId) {
        dbHelper.getWritableDatabase().delete(
                AppDatabaseHelper.TABLE_PLANNER,
                AppDatabaseHelper.COLUMN_PLANNER_DATE + " = ? AND " + AppDatabaseHelper.COLUMN_PLANNER_RECIPE_ID + " = ?",
                new String[]{date, recipeId});
    }

    public List<Recipe> getRecipesForDate(String date) {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_PLANNER, null,
                AppDatabaseHelper.COLUMN_PLANNER_DATE + " = ?", new String[]{date},
                null, null,
                AppDatabaseHelper.COLUMN_PLANNER_TITLE + " ASC")) {
            int idIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PLANNER_RECIPE_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PLANNER_TITLE);
            int imageUrlIndex = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PLANNER_IMAGE_URL);
            while (cursor.moveToNext()) {
                Recipe recipe = new Recipe(cursor.getString(idIndex), cursor.getString(titleIndex), null, null);
                recipe.setImageUrl(cursor.getString(imageUrlIndex));
                recipes.add(recipe);
            }
        }
        return recipes;
    }
}
