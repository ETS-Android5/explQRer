package com.example.explqrer;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.navigation.NavigationView;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Text;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ScanningActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        NavigationView navigationView= activity.findViewById(R.id.bottom_navigation_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.scan_nav);
        solo.clickOnMenuItem(item.toString());

    }

    @Test
    public void checkActivityChange(){
        solo.assertCurrentActivity("Wrong Activity",ScanningPageShow.class);
    }

    //TODO: test qr code scanned

    // TODO: test point show

    // TODO: test image capture ( if have time)



}

