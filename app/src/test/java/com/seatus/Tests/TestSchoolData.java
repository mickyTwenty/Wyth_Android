package com.seatus.Tests;

import com.seatus.Activities.SplashActivity;
import com.seatus.BaseClasses.BaseActivity;
import com.seatus.Models.SchoolItem;
import com.seatus.Models.VehicleMake;
import com.seatus.Retrofit.WebServiceFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by rohail on 5/28/2018.
 */
public class TestSchoolData {

    @Mock
    BaseActivity activity;
    private ArrayList<SchoolItem> response;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        response = WebServiceFactory.getInstance().getAllSchools().execute().body().body;
    }

    @After
    public void tearDown() throws Exception {
        response = null;
    }

    @Test
    public void validResponse() throws Exception {
        assertNotNull(response);
    }

    @Test
    public void validSchoolResponse() {
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

}