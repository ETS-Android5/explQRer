package com.example.explqrer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.robotium.solo.Solo;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setup() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        BottomNavigationView navigationView = activity.findViewById(R.id.bottom_navigation_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.map_nav);
        solo.clickOnMenuItem(String.valueOf(item));
    }

    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkActivityChange() {
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
    }

//    @Test
//    public void TestSearchLocation() {
//        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
//        solo.enterText((AutoCompleteTextView) solo.getView(R.id.search_location), " West edmonton mall");
//        View image1 = solo.getView("search_location_btn");
//        solo.clickOnView(image1);
//
//        // get search location set from activity
//        MapActivity activity = (MapActivity) solo.getCurrentActivity();
//        final double playerLongitude = activity.getPlayerLongitude();
//        String searchLongStr = String.format("%.4f", playerLongitude);
//        final double playerLatitude = activity.getPlayerLatitude();
//        String searchLatStr = String.format("%.4f", playerLatitude);
//
//        // check if the correct location
//        assertEquals( "wrong","-113.6230",searchLongStr);
//        assertEquals("wrong","53.5227", searchLongStr);
//    }
//
//    @Test
//    public void TestGoBackLocation() {
//        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
//        MapActivity activity = (MapActivity) solo.getCurrentActivity();
//        final double currentLongitude = activity.getPlayerLongitude();
//        final double currentLatitude = activity.getPlayerLatitude();
//
//        // go to another location
//        solo.enterText((AutoCompleteTextView) solo.getView(R.id.search_location), " West edmonton mall");
//        View image1 = solo.getView("search_location_btn");
//        solo.clickOnView(image1);
//
//        // check if location back
//        View view = solo.getView("recenter_location");
//        solo.clickOnView(view);
//
//        Assert.assertEquals("wrong", activity.getPlayerLongitude(), currentLongitude, 0.00);
//        Assert.assertEquals("wrong", activity.getPlayerLatitude(), currentLatitude, 0.00);
//    }


    /**
     * Closes the activity after each test
     *
     * @throws Exception
     **/
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}

