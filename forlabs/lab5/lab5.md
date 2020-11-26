# Лабораторная работа №5. UI Tests.

## Цели
- Ознакомиться с принципами и получить практические навыки разработки UI тестов для Android приложений.

### Задача. UI Тесты
Я сделал несколько тестов, которыми пытался покрыть все возможные придуманные мной ситуации.
#### Краткая теория
Espresso-тесты работают по следующей схеме
1. Находим `View` по `Matcher`
2. Выполняем `ViewAction`
3. Проверяем результат при помощи `ViewAssertion`

View - элемент на экране<br>
Matcher - штука (интерфейс), которая помогает найти желаемый `View` на экране: как по `id` так и по, например, какому-нибудь тексту.<br>
ViewAction - действие, которое можно совершить над `View`. Например, "кликнуть.<br>
ViewAssertion - проверяет, что взятое `View` совпадает с "ожидаемым". Например, совпадает текст.<br>
#### Пример 
`onView(withId(R.id.my_view)).check(matches(withText("Hello")))`
- `with(id)` возвращает `Matcher` по `id`
- `onView()` возвращает первый объект для `ViewAssertion`
- `matcher()` возвращает второй объект для `ViewAssertion`
- `check()` сравнивает 2 объекта `ViewAssertion`
<br>
#### Пролог
Я выделил отдельные методы с функционалом создания, изменения и удаления напоминания. Эти методы содержат в себе UI-тесты.

##### Создание напоминания
```
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
```
##### Изменение напоминания
```
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
```
##### Удаление напоминания
```
 private fun removeRem() {
        //переходим в раздел "edit reminder"
        //проверяем функцию удаления - удаляем созданное тестовое напоминание
        onView(withText("test edited text"))
            .perform(click())
        Intents.intended(hasComponent(EditingReminderActivity::class.java.name), times(2))

        onView(withId(R.id.remove_button))
            .perform(click())
}
```
В тестах есть моменты, когда экран нужно перевернуть. Это делается с помощью библиотеки `UIAutomator`
```
//используется библиотека UIAutomator
private fun rotateDeviceLeft() {
    val device = UiDevice.getInstance(getInstrumentation())
    device.setOrientationLeft()
}
private fun removeRotationDevice() {
    val device = UiDevice.getInstance(getInstrumentation())
    device.setOrientationNatural()
}
```

##### Работа с Activity
Прежде чем проверять переключение Activity в Espresso, нужно определить `Intents.init()` и `Intents.release()`
```
@Before
fun startUp() {
    Intents.init()
}
@After
fun tearDown() {
    Intents.release()
}
```
`Intents.init()` запоминает все переходы по `Activity` с помощью `Intents` в процессе теста.
`Intents.release()` очищает состояние `Intents`

Метки `@Before` и `@After` нужны для того, чтобы методы запускались, соответсвенно, до и после каждой проверки вроде этой:
`Intents.intended(hasComponent(MyRemindersActivity::class.java.name))`
<br>

#### Тест 1. Работа с напоминанием: создание, изменение, удаление
В тесте существляется переход в `AddingReminderActivity`. Нажимается "Create" в результате чего появляется Toast с просьбой заполнить все поля перед нажатием. Заполняем все поля, создаём напоминание. Затем проверяем что Activity сменилось на `MainActivity` - вернулись на главный экран. Используем для этого `Intents.intended(hasComponent(MainActivity::class.java.name), times(2))`<br>
Дальше переходим в "MyReminders" параллельно проверяем что Activity сменилось. Затем нажимаем на созданное тестовое напоминание с помощью `onView(withText("test text")).perform(click())` - переходим в `EditingReminderActivity`. Изменяем текст напоминания, нажимаем "Save" - возвращаемся в "MyReminders" и видим, что всё изменилось. Затем снова нажимаем на тестовое напоминание, попадаем в `EditingReminderActivity` и нажимаем "REMOVE". После нажатия возвращаемся в "MyReminders" и видим, что тестовое напоминание удалилось. Затем с помощью `pressBack()` нажимаем на андроидовскую кнопку-треугольник "назад" и возвращаемся на главный экран в `MainActivity` - проверяем backstack.<br>
![working_withrem_test.gif](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab5/screenshots/working_withrem_test.gif)<br>
Рис. 1. Работа с напоминанием: создание, изменение, удаление<br>
```
/**
* Проверяем функции добавления, изменения и удаления напоминания
 * */
 @Test
fun workingWithReminderTest() {
    workWithReminder()
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
```
<br>

#### Тест 2. Работа с напоминанием при landscape-orientation
Тоже самое, что и в Тесте 1, только с landscape-ориентацией экрана.<br>
![landscape_test.gif](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab5/screenshots/landscape_test.gif)<br>
Рис. 2. Работа с напоминанием: создание, изменение, удаление<br>
```
/**
 * Проверяем функции добавления, изменения и удаления напоминания
 * при горизонтальной ориентациии экрана
 * */
@Test
fun landscapeOrientationTest() {
    rotateDeviceLeft()
    workWithReminder()
}
```
<br>

#### Тест 3. Стресс-тест
Тоже самое, что и в Тесте 1 и Тесте 2, только в этот раз экран поворачивается в середине работы, например, чтобы проверить все ли введенные данные сохраняются при повороте экрана (при повороте экрана состояние не меняется)<br>
![stress_test.gif](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab5/screenshots/stess_test.gif)<br>
Рис. 2. Стресс-тест<br>
```
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
```

### Выводы
В данной работе мы ознакомиль с принципами и получили практические навыки разработки UI тестов для Android приложений: научились нажимать на кнопки, находить `View` с помощью `Matcher` и совершать разные действия с `View` на экране, сравнивать `View` при помощи `ViewAssertion`. Всё это можно сделать с помощью `Espresso` - очень удобного фреймворка для написания UI-тестов под андроид.

### Приложение
- UI-Тесты: [UITests.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/androidTest/java/com/beathunter/easyreminder/UITests.kt)

