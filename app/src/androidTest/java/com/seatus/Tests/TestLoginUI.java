package com.seatus.Tests;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.seatus.Activities.AccountsActivity;
import com.seatus.Fragments.LoginFragment;
import com.seatus.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class TestLoginUI {


    @Rule
    public ActivityTestRule<AccountsActivity> signInActivityActivityTestRule = new ActivityTestRule<>(AccountsActivity.class);


    @Test
    public void CheckviewVisibility() {

        onView(withId(R.id.field_email)).perform(clearText(), typeText("qq@qq.qq"));
        onView(withId(R.id.field_password)).perform(clearText(), typeText("qqqqqq"));
        onView(withId(R.id.btn_login)).perform(click());

//        ((LoginFragment) signInActivityActivityTestRule.getActivity().getCurrentFragment()).loginObserver.observe(signInActivityActivityTestRule.getActivity(), webResponseResource -> {
//            if (webResponseResource != null)
//                Log.e("Observer Status", webResponseResource.status.toString());
//        });
    }

}
