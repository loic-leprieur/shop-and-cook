package be.vives.loic.shopandcook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.vives.loic.shopandcook.models.Recipe;
import be.vives.loic.shopandcook.storage.FavoritesDao;
import be.vives.loic.shopandcook.storage.PlannerDao;
import be.vives.loic.shopandcook.storage.ShoppingListDao;

@RunWith(AndroidJUnit4.class)
public class StorageInstrumentedTest {
    private static final String DATE = "2099-01-02";

    private FavoritesDao favorites;
    private ShoppingListDao shopping;
    private PlannerDao planner;
    private Recipe recipe;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        favorites = new FavoritesDao(context);
        shopping = new ShoppingListDao(context);
        planner = new PlannerDao(context);
        recipe = new Recipe("test-1", "Test pasta", null, null);
        recipe.setImageUrl("https://example.com/pasta.jpg");
        cleanUp();
    }

    @After
    public void cleanUp() {
        favorites.remove("test-1");
        shopping.remove("1 kg test flour");
        planner.removeRecipeFromDate(DATE, "test-1");
    }

    @Test
    public void favoriteRoundTrip() {
        assertFalse(favorites.isFavorite("test-1"));
        favorites.add(recipe);
        assertTrue(favorites.isFavorite("test-1"));
        favorites.add(recipe); // adding twice must not duplicate
        long count = favorites.getAll().stream().filter(r -> r.getId().equals("test-1")).count();
        assertEquals(1, count);
        favorites.remove("test-1");
        assertFalse(favorites.isFavorite("test-1"));
    }

    @Test
    public void shoppingListRoundTrip() {
        shopping.add("1 kg test flour");
        shopping.add("1 kg test flour");
        assertEquals(1, shopping.getAll().stream().filter("1 kg test flour"::equals).count());
        shopping.remove("1 kg test flour");
        assertFalse(shopping.getAll().contains("1 kg test flour"));
    }

    @Test
    public void plannerKeepsRecipesPerDate() {
        assertTrue(planner.addRecipeToDate(DATE, recipe));
        assertEquals(1, planner.getRecipesForDate(DATE).size());
        assertEquals("Test pasta", planner.getRecipesForDate(DATE).get(0).getTitle());
        assertTrue(planner.getRecipesForDate("2099-01-03").isEmpty());
        planner.removeRecipeFromDate(DATE, "test-1");
        assertTrue(planner.getRecipesForDate(DATE).isEmpty());
    }
}
