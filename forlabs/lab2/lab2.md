## Лабораторная работа №2. Activity Lifecycle. Alternative resources.

### Цели
- Ознакомиться с жизненным циклом Activity
- Изучить основные возможности и свойства alternative resources

## Ход работы

### Activity
1. Ответ на входящий вызов<br>
![Рис. 1 Lifecycle при ответе на входящий вызов](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab2/screenshots/1%20(answering%20incoming%20call).png)<br> 
Рис. 1 Lifecycle при ответе на входящий вызов
2. Поворот экрана<br>
![Рис. 2 Lifecycle при повороте экрана](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab2/screenshots/2%20(changing%20device%20orientation).png)<br> 
Рис. 2 Lifecycle при повороте экрана
3. Смена приложения и обратно<br>
![Рис. 3 Lifecycle при смене приложения и обратно](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab2/screenshots/3%20(changing%20main%20app).png)<br> 
Рис. 3 Lifecycle при смене приложения и обратно

### Alternative Resourses
Для приложения создано несколько альтернативных ресурсов
1. Дополнительные layout'ы для горизонтального положения экрана<br>
![Рис. 1 Landscape orientation](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab2/screenshots/4%20(altres_land-en).png)<br> 
Рис. 1 Landscape orientation

2. Дополнительные layout'ы для русского языка<br>
![Рис. 2 Russian language support](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab2/screenshots/5%20(altres_ru).png)<br> 
Рис. 2 Russian language support<br>
![Рис. 3 Russian language support + landscape orientation](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab2/screenshots/6%20(altres_land-ru).png)<br> 
Рис. 3 Russian language support + landscape orientation
### Best-matching resource
Для заданного набора альтернативных ресурсов, предоставляемых приложением, и заданной конфигурации устройства объясните, какой ресурс будет выбран в конечном итоге. <br>

**20 вариант**
#### Конфигурация устройства
```
LOCALE: en-rUS
SCREEN_SIZE: xlarge
SCREEN_ASPECT: notlong
ROUND_SCREEN: notround
ORIENTATION: land
UI_MODE: car
NIGHT_MODE: night
PIXEL_DENSITY: xhdpi
TOUCH: notouch
PRIMARY_INPUT: nokeys
NAV_KEYS: trackball
PLATFORM_VER: v25
```
#### Конфигурация ресурсов
```
(default)
fr-rCA-small-watch-night
port-nokeys-v27
port-night-nodpi-qwerty
normal-round
notlong-round-port-notouch-wheel-v27
normal-television-nokeys
finger-12key-dpad
notround-finger-nokeys
notouch
large-notlong-notround-television-notnight-12key-trackball
```
##### Шаг 1 
Исключаем французский язык **fr-rCA**, т.к. в конфиге устройства - английский (США) **en-rUS**

(default)<br>
**~~fr-rCA-small-watch-night~~**<br>
port-nokeys-v27<br>
port-night-nodpi-qwerty<br>
normal-round<br>
notlong-round-port-notouch-wheel-v27<br>
normal-television-nokeys<br>
finger-12key-dpad<br>
notround-finger-nokeys<br>
notouch<br>
large-notlong-notround-television-notnight-12key-trackball<br>

##### Шаг 2
Исключаем круглый экран **round**, т.к. в конфиге устройства - **notround**

(default)<br>
port-nokeys-v27<br>
port-night-nodpi-qwerty<br>
**~~normal-round~~**<br>
**~~notlong-round-port-notouch-wheel-v27~~**<br>
normal-television-nokeys<br>
finger-12key-dpad<br>
notround-finger-nokeys<br>
notouch<br>
large-notlong-notround-television-notnight-12key-trackball<br>

##### Шаг 3
Исключаем **UI mode television**, т.к. в конфиге устройства - **car**

(default)<br>
port-nokeys-v27<br>
port-night-nodpi-qwerty<br>
**~~normal-television-nokeys~~**<br>
finger-12key-dpad<br>
notround-finger-nokeys<br>
notouch<br>
**~~large-notlong-notround-television-notnight-12key-trackball~~**<br>
##### Шаг 4
Исключаем ориентацию устройства **port**, т.к. в конфиге устройства - **land**

(default)<br>
**~~port-nokeys-v27~~**<br>
**~~port-night-nodpi-qwerty~~**<br>
finger-12key-dpad<br>
notround-finger-nokeys<br>
notouch<br>

##### Шаг 5
Исключаем  **Touchscreen type finger**, т.к. в конфиге устройства - **notouch**

(default)<br>
**~~finger-12key-dpad~~**<br>
**~~notround-finger-nokeys~~**<br>
notouch<br>

Единственная оставшая альтернативная конфигурация - **notouch**. Она и будет выбрана.

### Continuewatch (Сохранение состояния Activity.)
"Студент написал приложение: continuewatch. Это приложение по заданию должно считать, сколько секунд пользователь провел в этом приложении."

Студент написал приложение, не соответсвующее условию задачи. Оно продолжает считать секунды, если приложение не активно. Также при повороте экрана приложение сбрасывает таймер и начинает считать заново. Также при включении приложения, в самом начале работы, до старта таймера, выскакивает надпись **Hello World!**

#### Ошибки студента
1. Не переопределены методы `onResume()` и `onPause()`
2. Не переопределены методы `onSaveInstanceState()` и `onRestoreInstanceState()`
3. Не убран атрибут `text` у `textSecondsElapsed : TextView`

#### Решение
1. Добавлен флаг `var isRunning = false`, который обновляется в переопределенных методах `onResume()`
и `onPause()`

    ```
    override fun onResume() {
            super.onResume()
            isRunning = true
            Log.d(TAG, "Activity onResume(): resumed")
        }

    override fun onPause() {
            super.onPause()
            isRunning = false
            Log.d(TAG, "Activity onPause(): paused")
        }
    ```

    Вместе с этим изменено содержание потока `backgroudThread` - добавлено условие с флагом `isRunning`

    ```
    var backgroundThread = Thread {
            while (true) {
                Thread.sleep(1000)
                if (isRunning) {
                    textSecondsElapsed.post {
                        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                    }
                }
            }
        }
    ```
    Все эти изменения позволяют приостановить таймер, когда приложение не активно и возобновить таймер когда приложение снова активно.
2. Переопределены методы `onSaveInstanceState()` и `onRestoreInstanceState()`
    ```
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seconds", secondsElapsed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        secondsElapsed = savedInstanceState.getInt("seconds")
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed)
        }
    }
    ```
    При повороте экрана происходит следующая последовательность действий у Activity Lifecycle
    ```
    onPause()
    onStop()
    onDestroy()
    onCreate()
    onStart()
    onResume()
    ```
    То есть при повороте экрана **Activity** полностью уничтожается и создается заново.

    При помощи `onSaveInstanceState()` мы запоминаем значение переменной `secondsElapsed` (количество прошедших секунд), чтобы затем, после создания **Activity** (после выполнения **onCreate()**) восстановить при помощи `onRestoreInstanceState()`. Таким образом, при повороте экрана, мы сохраним количество прошедшихз секунд и таймер продолжит считать дальше.
3. Убран атрибут `text = HelloWorld!` у `textSecondsElapsed : TextView`
### Вывод
В результате работы изучен жизненный цикл **Activity**. Изучены и созданы альтернативные ресурсы для приложения - для `ORIENTATION:land` и `LOCALE:ru`. Изучен алгоритм **best-mathcing resourse**. В предоставленном приложении найдены и исправлены ошибки, связанные с **Activity Lifecycle**.

### Приложение

#### layout-land/adding_reminder.xml

```
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">


    <ImageView
        android:id="@+id/bg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:srcCompat="@drawable/bg" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="horizontal">


        <EditText
            android:id="@+id/editTextTextPersonName"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:ems="10"
            android:layout_weight="1"
            android:layout_marginHorizontal="20dp"
            android:layout_marginVertical="20dp"
            android:layout_gravity="center"
            android:inputType="textPersonName"
            android:text="write your reminding here"
            android:textAlignment="center" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:layout_weight="1"
            android:layout_marginHorizontal="20dp">

            <Button
                android:id="@+id/set_date_button"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="#000000"
                android:textSize="30sp"
                android:layout_marginTop="30dp"
                android:layout_weight="0"
                android:layout_gravity="center"
                android:background="@drawable/buttonshape"
                android:onClick="onSelectDateButtonClick"
                android:text="set date"
                android:shadowColor="#A8A8A8"
                android:shadowDx="0"
                android:shadowDy="0"
                android:shadowRadius="5"/>

            <Button
                android:id="@+id/set_time_button"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="#000000"
                android:textSize="30sp"
                android:layout_marginTop="30dp"
                android:layout_weight="0"
                android:layout_gravity="center"
                android:background="@drawable/buttonshape"
                android:onClick="onSelectTimeButtonClick"
                android:text="set time"
                android:shadowColor="#A8A8A8"
                android:shadowDx="0"
                android:shadowDy="0"
                android:shadowRadius="5"/>

            <Button
                android:id="@+id/create_button"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:layout_marginTop="30dp"
                android:layout_weight="0"
                android:background="@drawable/buttonshape"
                android:onClick="onCreateButtonClick"
                android:shadowColor="#A8A8A8"
                android:shadowDx="0"
                android:shadowDy="0"
                android:shadowRadius="5"
                android:text="create"
                android:textColor="#000000"
                android:textSize="30sp" />
        </LinearLayout>
    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### layout-ru-land/activity_main.xml

```
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/bg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:srcCompat="@drawable/bg" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="horizontal">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/textView"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="10dp"
                android:layout_gravity="center"
                android:text="Nearest"
                android:textAlignment="center"
                android:textColor="#C1C1C1"
                android:textSize="30sp"
                android:textStyle="bold"
                app:layout_constraintEnd_toEndOf="@+id/bg"
                app:layout_constraintStart_toStartOf="@+id/bg"
                app:layout_constraintTop_toTopOf="@+id/bg" />

            <ScrollView
                android:id="@+id/ScrollView_maincreen_land"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_marginHorizontal="30dp"
                android:layout_marginVertical="10dp"
                android:scrollbars="none">

                <LinearLayout
                    android:id="@+id/hLinLayout"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center"
                    android:orientation="vertical" />
            </ScrollView>
        </LinearLayout>


        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:orientation="vertical">

            <Button
                android:id="@+id/add_rem_button"
                android:layout_width="200dp"
                android:layout_height="200dp"
                android:layout_gravity="center"
                android:layout_margin="30dp"
                android:background="@drawable/add_rem_button"
                android:onClick="onAddRemButtonClick"
                app:layout_constraintVertical_bias="0.559" />

            <Button
                android:id="@+id/my_rems_button"
                android:layout_margin="20dp"
                android:layout_marginStart="20dp"
                android:layout_marginEnd="20dp"
                android:background="@drawable/buttonshape"
                android:onClick="onMyRemsButtonClick"
                app:layout_constraintVertical_bias="0.20"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="#000000"
                android:textSize="30sp"
                android:layout_marginTop="30dp"
                android:layout_weight="0"
                android:layout_gravity="center"
                android:text="Мои напоминания"
                android:shadowColor="#A8A8A8"
                android:shadowDx="0"
                android:shadowDy="0"
                android:shadowRadius="5"/>
        </LinearLayout>
    </LinearLayout>


</androidx.constraintlayout.widget.ConstraintLayout>
```

#### continuewatch/MainActivity.kt

```
package ru.spbstu.icc.kspt.lab2.continuewatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG : String = "lifecycle"
    var secondsElapsed: Int = 0
    var isRunning = false

    var backgroundThread = Thread {
        while (true) {
            Thread.sleep(1000)
            if (isRunning) {
                textSecondsElapsed.post {
                    textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        backgroundThread.start()
        Log.d(TAG, "Activity onCreate(): created")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Activity onStart(): started")
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
        Log.d(TAG, "Activity onResume(): resumed")
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
        Log.d(TAG, "Activity onPause(): paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Activity onStop(): stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity onDestroy(): destroyed")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seconds", secondsElapsed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        secondsElapsed = savedInstanceState.getInt("seconds")
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed)
        }
    }
}

```

