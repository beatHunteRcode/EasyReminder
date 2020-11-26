package com.beathunter.easyreminder

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.beathunter.easyreminder.Activities.AddingReminderActivity
import com.beathunter.easyreminder.Activities.EditingReminderActivity
import com.beathunter.easyreminder.Activities.MainActivity
import com.beathunter.easyreminder.Activities.MyRemindersActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class UITests {

    @Before
    fun startUp() {
        Intents.init()
    }
    @After
    fun tearDown() {
        Intents.release()
    }

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Проверяем функции добавления, изменения и удаления напоминания
     * */
    @Test
    fun workingWithReminderTest() {
        workWithReminder()
    }

    /**
     * Проверяем функции добавления, изменения и удаления напоминания
     * при горизонтальной ориентациии экрана
     * */
    @Test
    fun landscapeOrientationTest() {
        rotateDeviceLeft()
        workWithReminder()
    }

    /**
     * Стресс-тест функционала при поворотах экрана в разных ситуациях
     * */
    @Test
    fun stressRotationTest() {
        createRem(true)
        onView(withId(R.id.my_rems_button)).perform(click())
        editRem(true)
        removeRem()
    }

    private fun workWithReminder() {
        createRem(false)
        Intents.intended(hasComponent(MainActivity::class.java.name))
        //переходим в раздел "my reminders"
        //проверяем что созданное напоминание добавилось в список всех напоминаний
        onView(withId(R.id.my_rems_button))
            .perform(click())
        Intents.intended(hasComponent(MyRemindersActivity::class.java.name))
        editRem(false)
        Intents.intended(hasComponent(MyRemindersActivity::class.java.name), times(2))
        removeRem()
        Intents.intended(hasComponent(MyRemindersActivity::class.java.name), times(3))
        pressBack()
        Intents.intended(hasComponent(MainActivity::class.java.name), times(2))
    }

    private fun createRem(rotate : Boolean) {
        //нажимаем на кнопку "add reminder"
        //и проверяем что Activity сменилось на AddingReminderActivity
        onView(withId(R.id.add_rem_button))
            .perform(click())
        Intents.intended(hasComponent(AddingReminderActivity::class.java.name))

        if (rotate) rotateDeviceLeft()
        //нажимаем на "CREATE" и проверяем что остались на AddingReminderActivity
        //должен появиться Toast с просьбой заполнить все поля перед нажатием на "CREATE"
        onView(withId(R.id.create_button))
            .perform(click())
        Intents.intended(hasComponent(AddingReminderActivity::class.java.name))

        //проверяем заполнение всех полей и нажатие "CREATE"
        onView(withId(R.id.reminding_text))
            .perform(typeText("test text"))
        closeSoftKeyboard()
        onView(withId(R.id.set_date_button)).perform(click())
        onView(withId(R.id.set_date_button)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.set_time_button)).perform(click())
        onView(withText("OK")).perform(click())

        if (rotate) rotateDeviceLeft()
        onView(withId(R.id.create_button))
            .perform(click())
    }

    private fun editRem(rotate : Boolean) {
        if (rotate) rotateDeviceLeft()
        onView(withText("test text"))
            .perform(click())
        Intents.intended(hasComponent(EditingReminderActivity::class.java.name))
        onView(withText("test text")).perform(typeText(" edited"))
        closeSoftKeyboard()
        if (rotate) removeRotationDevice()
        onView(withId(R.id.save_button))
            .perform(click())
    }

    private fun removeRem() {
        //переходим в раздел "edit reminder"
        //проверяем функцию удаления - удаляем созданное тестовое напоминание
        onView(withText("test edited text"))
            .perform(click())
        Intents.intended(hasComponent(EditingReminderActivity::class.java.name), times(2))

        onView(withId(R.id.remove_button))
            .perform(click())
    }

    //используется библиотека UIAutomator
    private fun rotateDeviceLeft() {
        val device = UiDevice.getInstance(getInstrumentation())
        device.setOrientationLeft()
    }
    private fun removeRotationDevice() {
        val device = UiDevice.getInstance(getInstrumentation())
        device.setOrientationNatural()
    }
}
