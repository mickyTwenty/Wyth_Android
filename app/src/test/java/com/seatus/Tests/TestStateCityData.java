package com.seatus.Tests;

import android.util.Log;

import com.google.gson.Gson;
import com.seatus.BaseClasses.BaseActivity;
import com.seatus.Models.CityItem;
import com.seatus.Models.StateItem;
import com.seatus.Utils.StaticMethods;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by rohail on 5/28/2018.
 */
public class TestStateCityData {

    @Mock
    BaseActivity context;
    private ArrayList<StateItem> listStates;
    private ArrayList<CityItem> listCity;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        String statesJson = StaticMethods.readStringFile(context, getFileFromPath(this, "states.json"));
        String cityJson = StaticMethods.readStringFile(context, getFileFromPath(this, "cities.json"));

        listStates = StaticMethods.getArrayListFromJson(new Gson(), statesJson, StateItem.class);
        listCity = StaticMethods.getArrayListFromJson(new Gson(), cityJson, CityItem.class);

    }

    @After
    public void tearDown() throws Exception {
        listStates = null;
        listCity = null;
    }

    @Test
    public void validStates() throws Exception {
        assertFalse(listStates.isEmpty());
    }

    @Test
    public void validCities() {
        assertFalse(listCity.isEmpty());
    }

    @Test
    public void hasCitiesForEveryState() {
        boolean testCaseStatus = true;
        for (StateItem state : listStates) {
            boolean stateStatus = false;
            for (CityItem city : listCity)
                if (city.state_id.equals(state.id)) {
                    stateStatus = true;
                    break;
                }

            if (!stateStatus) {
                System.out.println("No Cities " + state.name + " id:" + state.id);
                testCaseStatus = false;
            }
        }
        assertTrue(testCaseStatus);
    }

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

}