package com.example.androiduitesting;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShowActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testActivitySwitch() {
        // Add a city first in MainActivity
        onView(withId(R.id.button_add)).perform(click());
        onView(withId(R.id.editText_name)).perform(ViewActions.typeText("Calgary"));
        onView(withId(R.id.button_confirm)).perform(click());

        // Click on the city in the list
        onData(is(instanceOf(String.class)))
                .inAdapterView(withId(R.id.city_list))
                .atPosition(0)
                .perform(click());

        // Verify that ShowActivity was launched
        Intents.intended(IntentMatchers.hasComponent(ShowActivity.class.getName()));
    }

    @Test
    public void testCityNameConsistency() {
        // Add a city in MainActivity
        onView(withId(R.id.button_add)).perform(click());
        onView(withId(R.id.editText_name)).perform(ViewActions.typeText("Toronto"));
        onView(withId(R.id.button_confirm)).perform(click());

        // Click on the city in the list to navigate to ShowActivity
        onData(is(instanceOf(String.class)))
                .inAdapterView(withId(R.id.city_list))
                .atPosition(0)
                .perform(click());

        // Verify the city name is displayed correctly in ShowActivity
        onView(withId(R.id.textView_cityName)).check(matches(withText("Toronto")));
    }

    @Test
    public void testBackButton() {
        // Add a city in MainActivity
        onView(withId(R.id.button_add)).perform(click());
        onView(withId(R.id.editText_name)).perform(ViewActions.typeText("Montreal"));
        onView(withId(R.id.button_confirm)).perform(click());

        // Click on the city in the list to open ShowActivity
        onData(is(instanceOf(String.class)))
                .inAdapterView(withId(R.id.city_list))
                .atPosition(0)
                .perform(click());

        // Click the back button in ShowActivity
        onView(withId(R.id.button_back)).perform(click());

        // Verify we're back to MainActivity by checking if the add button is visible
        onView(withId(R.id.button_add)).check(matches(isDisplayed()));
    }

    @Test
    public void testShowActivityDirectLaunch() {
        // Test launching ShowActivity directly with intent
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ShowActivity.class);
        intent.putExtra("CITY_NAME", "Ottawa");

        ActivityScenario<ShowActivity> scenario = ActivityScenario.launch(intent);

        // Verify the city name is displayed correctly
        onView(withId(R.id.textView_cityName)).check(matches(withText("Ottawa")));

        // Verify the back button is displayed
        onView(withId(R.id.button_back)).check(matches(isDisplayed()));

        scenario.close();
    }
}
