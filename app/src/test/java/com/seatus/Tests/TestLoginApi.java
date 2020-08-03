package com.seatus.Tests;

import com.google.gson.Gson;
import com.seatus.Activities.SplashActivity;
import com.seatus.BaseClasses.BaseActivity;
import com.seatus.Models.CityItem;
import com.seatus.Models.SchoolItem;
import com.seatus.Models.StateItem;
import com.seatus.Models.UserItem;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.StaticMethods;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Response;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by rohail on 5/28/2018.
 */
public class TestLoginApi {

    @Mock
    BaseActivity context;

    WebResponse<UserItem> response;

    @Before
    public void setUp() throws Exception {
        String email = "qq@qq.qq";
        String password = "qqqqqq";

        response = WebServiceFactory.getInstance().login(email, password, "12345678910", "android").execute().body();
    }

    @After
    public void tearDown() throws Exception {
        response = null;
    }

    @Test
    public void isLoginServerResponding() throws Exception {
        assertNotNull(response);
    }

//    @Test
//    public void isLoginSuccesfull() throws Exception {
//        assertTrue(response.isSuccess());
//    }

}