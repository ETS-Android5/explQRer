package com.example.explqrer;

import org.junit.*;
import static org.junit.Assert.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import android.app.Activity;
import androidx.test.platform.app.InstrumentationRegistry;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import androidx.test.ext.junit.rules.ActivityScenarioRule;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */

public class MainActivityTests {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);


    // TODO: Write UI Tests

    /**
     * Runs before all tests and creates solo instance. (necessary for test case to access)
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), activityRule.getActivity());
    }

    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = solo.getCurrentActivity();
    }



    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
