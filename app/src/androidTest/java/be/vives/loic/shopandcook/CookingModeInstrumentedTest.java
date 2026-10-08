package be.vives.loic.shopandcook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import be.vives.loic.shopandcook.activities.CookingModeActivity;

@RunWith(AndroidJUnit4.class)
public class CookingModeInstrumentedTest {
    @Test
    public void walksThroughStepsAndFinishes() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, CookingModeActivity.class)
                .putExtra(CookingModeActivity.EXTRA_TITLE, "Test dish")
                .putStringArrayListExtra(CookingModeActivity.EXTRA_STEPS,
                        new ArrayList<>(Arrays.asList("Chop", "Fry")));

        try (ActivityScenario<CookingModeActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.stepCounter)).check(matches(withText("Step 1 / 2")));
            onView(withId(R.id.stepText)).check(matches(withText("Chop")));
            onView(withId(R.id.prevButton)).check(matches(not(isEnabled())));

            onView(withId(R.id.nextButton)).perform(click());
            onView(withId(R.id.stepText)).check(matches(withText("Fry")));
            onView(withId(R.id.nextButton)).check(matches(withText(R.string.finish)));

            onView(withId(R.id.prevButton)).perform(click());
            onView(withId(R.id.stepText)).check(matches(withText("Chop")));
        }
    }
}
