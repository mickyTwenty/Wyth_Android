package com.seatus.Tests;

import android.arch.lifecycle.ViewModelProviders;

import com.seatus.Activities.SplashActivity;
import com.seatus.BaseClasses.BaseActivity;
import com.seatus.Models.BootMeUpResponse;
import com.seatus.Models.VehicleMake;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.ViewModels.ActivityViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import retrofit2.Response;

import static org.junit.Assert.*;

/**
 * Created by rohail on 5/28/2018.
 */
public class TestBootMeUp {

    @Mock
    BaseActivity activity;
    private BootMeUpResponse response;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        response = WebServiceFactory.getInstance().bootMeUp().execute().body().body;
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
    public void validVehicleTypes() {
        assertNotNull(response.vehicle_type);
        assertFalse(response.vehicle_type.isEmpty());
    }

    @Test
    public void validEstimateRates() {
        assertNotEquals(response.min_estimate, 0.0f);
        assertNotEquals(response.max_estimate, 0.0f);
    }

    @Test
    public void validVehicleMakes() {
        assertFalse(response.make.isEmpty());
    }

    @Test
    public void validVehicleModelsInMake() {
        boolean status = true;
        for (VehicleMake make : response.make)
            if (make.models.isEmpty())
                status = false;
        assertTrue(status);
    }

    @Test
    public void validReferenceSources(){
        assertNotNull(response.reference_source);
        assertFalse(response.reference_source.isEmpty());
    }

}