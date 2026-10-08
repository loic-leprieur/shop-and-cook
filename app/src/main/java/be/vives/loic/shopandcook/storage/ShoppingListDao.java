package be.vives.loic.shopandcook.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListDao {
    private final AppDatabaseHelper dbHelper;

    public ShoppingListDao(Context context) {
        dbHelper = AppDatabaseHelper.getInstance(context);
    }

    public void add(String ingredient) {
        ContentValues values = new ContentValues();
        values.put(AppDatabaseHelper.COLUMN_INGREDIENT, ingredient);
        dbHelper.getWritableDatabase().insertWithOnConflict(
                AppDatabaseHelper.TABLE_SHOPPING_LIST, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void remove(String ingredient) {
        dbHelper.getWritableDatabase().delete(
                AppDatabaseHelper.TABLE_SHOPPING_LIST,
                AppDatabaseHelper.COLUMN_INGREDIENT + " = ?",
                new String[]{ingredient});
    }

    public List<String> getAll() {
        List<String> ingredients = new ArrayList<>();
        try (Cursor cursor = dbHelper.getReadableDatabase().query(
                AppDatabaseHelper.TABLE_SHOPPING_LIST, null, null, null, null, null,
                AppDatabaseHelper.COLUMN_INGREDIENT + " ASC")) {
            int index = cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_INGREDIENT);
            while (cursor.moveToNext()) {
                ingredients.add(cursor.getString(index));
            }
        }
        return ingredients;
    }
}
