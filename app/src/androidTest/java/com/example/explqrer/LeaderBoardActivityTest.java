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

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.robotium.solo.Solo;

@RunWith(AndroidJUnit4.class)
public class LeaderBoardActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * click the navigation bar at the bottom of the main page
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        BottomNavigationView navigationView = activity.findViewById(R.id.bottom_navigation_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.leaderboard_nav);
        solo.clickOnMenuItem(String.valueOf(item));
    }

    /**
     * Test activity change
     */
    @Test
    public void checkActivityChange() {
        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
    }


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
