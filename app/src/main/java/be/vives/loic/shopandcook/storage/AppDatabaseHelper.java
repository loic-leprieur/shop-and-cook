package be.vives.loic.shopandcook.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Local SQLite storage (internal app storage) for favorite recipes and the shopping list,
 * so both survive process death and app restarts.
 */
class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shopandcook.db";
    private static final int DATABASE_VERSION = 2;

    static final String TABLE_FAVORITES = "favorites";
    static final String COLUMN_FAVORITE_ID = "id";
    static final String COLUMN_FAVORITE_TITLE = "title";
    static final String COLUMN_FAVORITE_IMAGE_URL = "image_url";

    static final String TABLE_SHOPPING_LIST = "shopping_list";
    static final String COLUMN_INGREDIENT = "ingredient";

    static final String TABLE_PLANNER = "planned_recipes";
    static final String COLUMN_PLANNER_DATE = "date";
    static final String COLUMN_PLANNER_RECIPE_ID = "recipe_id";
    static final String COLUMN_PLANNER_TITLE = "title";
    static final String COLUMN_PLANNER_IMAGE_URL = "image_url";

    private static AppDatabaseHelper instance;

    static synchronized AppDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_FAVORITE_ID + " TEXT PRIMARY KEY, " +
                COLUMN_FAVORITE_TITLE + " TEXT NOT NULL, " +
                COLUMN_FAVORITE_IMAGE_URL + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_SHOPPING_LIST + " (" +
                COLUMN_INGREDIENT + " TEXT PRIMARY KEY)");

        db.execSQL("CREATE TABLE " + TABLE_PLANNER + " (" +
                COLUMN_PLANNER_DATE + " TEXT NOT NULL, " +
                COLUMN_PLANNER_RECIPE_ID + " TEXT NOT NULL, " +
                COLUMN_PLANNER_TITLE + " TEXT NOT NULL, " +
                COLUMN_PLANNER_IMAGE_URL + " TEXT, " +
                "PRIMARY KEY (" + COLUMN_PLANNER_DATE + ", " + COLUMN_PLANNER_RECIPE_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANNER);
        onCreate(db);
    }
}
