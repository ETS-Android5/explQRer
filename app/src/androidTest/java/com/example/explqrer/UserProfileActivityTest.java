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

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.robotium.solo.Solo;

@RunWith(AndroidJUnit4.class)
public class UserProfileActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        BottomNavigationView navigationView = activity.findViewById(R.id.bottom_navigation_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.profile_nav);
        solo.clickOnMenuItem(String.valueOf(item));
    }

    @Test
    public void checkActivityChange() {
        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);
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


    @Test
    public void checkProfileQR(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("DENY");
        solo.clickOnButton("DENY");
        solo.clickOnButton("Profile");
        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);
        solo.clickOnButton("Profile QR");

    }

}
