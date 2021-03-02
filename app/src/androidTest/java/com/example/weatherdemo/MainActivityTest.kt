package com.example.weatherdemo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.security.Permission
import java.util.jar.Manifest

@RunWith(AndroidJUnit4::class)
class MainActivityTest{

    private lateinit var stringToBetyped: String

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var grantCoarseLocationPermissionRule: GrantPermissionRule
            = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    @get:Rule
    var grantFineLocationPermissionRule: GrantPermissionRule
            = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun initValidString() {
        stringToBetyped = "Espresso"
    }

    @Test
    fun testEvent() {

        onView(withId(R.id.weather_updated)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_day_of_today)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_temp)).check(matches(isDisplayed()))
        onView(withId(R.id.feelslike)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_feellike)).check(matches(isDisplayed()))
        onView(withId(R.id.rain_drops)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_rain)).check(matches(isDisplayed()))
        onView(withId(R.id.wind)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_wind)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_today)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_desc)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_temp_max)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_temp_min)).check(matches(isDisplayed()))

        onView(withId(R.id.bottomSheet)).check(matches(isDisplayed()))

        onView(withId(R.id.activity_search)).check(matches(isDisplayed()))
        onView(withId(R.id.activity_search)).perform(click())

    }
}