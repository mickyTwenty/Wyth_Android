package com.seatus.Suits;

import com.seatus.Tests.TestBootMeUp;
import com.seatus.Tests.TestLoginApi;
import com.seatus.Tests.TestSchoolData;
import com.seatus.Tests.TestStateCityData;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by rohail on 5/28/2018.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TestBootMeUp.class, TestSchoolData.class,TestStateCityData.class,TestLoginApi.class})
public class DataPreLoadsSuite {
}
