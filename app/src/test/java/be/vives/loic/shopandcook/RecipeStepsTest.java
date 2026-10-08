package be.vives.loic.shopandcook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

import be.vives.loic.shopandcook.models.RecipeSteps;

public class RecipeStepsTest {
    @Test
    public void splitsOneStepPerLine() {
        assertEquals(Arrays.asList("Boil water.", "Add pasta."),
                RecipeSteps.split("Boil water.\r\n\r\nAdd pasta.\n"));
    }

    @Test
    public void ignoresStepHeadings() {
        assertEquals(Arrays.asList("Chop onions.", "Fry them."),
                RecipeSteps.split("STEP 1\nChop onions.\nstep 2\nFry them.\n3.\n"));
    }

    @Test
    public void splitsSingleParagraphOnSentences() {
        assertEquals(Arrays.asList("Mix flour and eggs.", "Rest for 10 minutes!", "Bake."),
                RecipeSteps.split("Mix flour and eggs. Rest for 10 minutes! Bake."));
    }

    @Test
    public void handlesEmptyAndNull() {
        assertTrue(RecipeSteps.split("").isEmpty());
        assertTrue(RecipeSteps.split(null).isEmpty());
    }
}
