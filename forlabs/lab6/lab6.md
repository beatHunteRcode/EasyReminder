# Лабораторная работа №6. Многопоточные Android приложения.

## Цели
Получить практические навыки разработки многопоточных приложений:
1. Организация обработки длительных операций в background (worker) thread:
    - Запуск фоновой операции (coroutine/asynctask/thread)
    - Остановка фоновой операции (coroutine/asynctask/thread)
2. Публикация данных из background (worker) thread в main (ui) thread.


Освоить 3 основные группы API для разработки многопоточных приложений:
1. Kotlin Coroutines
2. AsyncTask
3. Java Threads

## Задачи

### Задача 1. Альтернативные решения задачи "не секундомер" из Лаб. 2
#### Java Threads
Вернёмся ко 2ой лабораторной... Одной из её проблем были "лишние" треды. Как только активити пересоздавалась (например, при повороте экрана) - создавался новый `Thread`, который продолжал подсчёт секунд, а старый продолжал висеть в фоне и тратить ресурсы.
В данном пункте нам нужно избавиться от этой проблемы.
Решение:
1. В инициализацию треда добавляем условие, по которому он будет работать: `backgroundThread?.isInterrupted == false`
2. Перемещаем саму иницализацию треда в `onResume()`
3. Т.к. нам нужно, чтобы таймер останавливался при сворачивании приложения, а не только при разрушении активити, с помощью `backgroundThread?.interrupt()` удаляем лишний тред при сворачивании приложения. В самом деле треда выскочит исключение `InterruptedExeption`, которое мсы успешно ловим.
4. При разворачивании приложения (и при создании активити заново) создаём новый тред, который продолжает подсчёт времени.
<br>

Ключевая часть из [MainActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/1_continuewatch_(JavaThreads)/app/src/main/java/ru/spbstu/icc/kspt/lab2/continuewatch/MainActivity.kt)
```kotlin
override fun onResume() {
        super.onResume()
        Log.d(TAG, "Activity onResume(): resumed")

        backgroundThread = Thread {
            try {
                while (backgroundThread?.isInterrupted == false) {
                    Thread.sleep(1000)
                    textSecondsElapsed.post {
                        textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                        threadsTV.setText("Number of threads: " + Thread.getAllStackTraces().keys.size)
                    }
                }
            }
            catch (e: InterruptedException) {
                //ignored
            }
        }
        backgroundThread?.start()
}

override fun onPause() {
        super.onPause()
        Log.d(TAG, "Activity onPause(): paused")
        backgroundThread?.interrupt()
}
```
<br>

#### AsyncTask
##### Краткая теория
При создании AsyncTask необходимо указать 3 параметра
- Params - тип входных параметров для выполянемого процесса
- Progress - тип данных, возвращаемых в ходе работы процесса (например, прогресс progress-bar'a)
- Result - тип данных, возвращаемых процессом
<br>

Также в теле класса, который наследует AsyncTask можно переопределить следующие методы
- `doInBackground()` - в нём описывается то, что будет делать процесс
- `onProgressUpdate()` - то, что мы хотим делать во время работы AsyncTask'a
- `onPreExecute()` - то, что мы хотим сделать перед началом работы процесса
- `onPostExecute()` - то, что мы хотим сделать после завершения работы процесса
<br>

##### Решение
1. Создаём вложенный класс `MyTask`, наследуем `AsyncTask`, в конструктор вложенного класса передаём в качестве параметра - функцию - функцию обновления текста у `TextView`
2. Переопределяем `doInBackground()` и `onProgressUpdate()`
3. Также как и с Java Thread: в `onResume()` инициализируем `MyTask` и запускаем процесс через `myTask?.execute()`, а в `onPause()` останавливаем процесс через `myTask?.cancel(false)`
<br>

В этот раз нам не нужно создавать дополнительный флаг, чтобы избежать `InterruptedException()`, так как у нас есть заготовленный флаг `AsyncTask'a` - `isCancelled`, что весьма удобно.

Ключевая часть из [MainActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/2_continuewatch_(AsyncTask)/app/src/main/java/ru/spbstu/icc/kspt/lab2/continuewatch/MainActivity.kt)

Вложенный класс
```kotlin
class MyTask(val func : () -> Boolean) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg p0: Unit?) {
        while (!isCancelled) {
            Thread.sleep(1000)
            publishProgress()
        }
    }
    override fun onProgressUpdate(vararg values: Unit?) {
        super.onProgressUpdate(*values)
        func()
    }
}
```

`onResume()` и `onPause()`
```kotlin
var myTask : MyTask? = null

override fun onResume() {
    super.onResume()
    Log.d(TAG, "Activity onResume(): resumed")
    myTask = MyTask {
        textSecondsElapsed.post {
            textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
            threadsTV.setText("Number of threads: " + Thread.getAllStackTraces().keys.size)
        }
    }
    myTask?.execute()
}

override fun onPause() {
    super.onPause()
    Log.d(TAG, "Activity onPause(): paused")
    myTask?.cancel(false)
}
```
<br>

#### Kotlin Coroutines
Корутины пришли на замену *callback'ам*. По сути, под капотом корутин остались всё те же колбэки, но корутины дали возможность писать код менее громоздко, делая его лучше читаемым, что очень удобно.

Запускается корутина с помощью `CoroutineScope.launch()`

`CoroutineScope` - интерфейс который определяет жизненные рамки корутины
`launch()` - метод для асинхронного запуска корутины

Также при запуске корутины можно указать диспетчера - `Dispatcher`. Диспетчер определяет какой поток будет использован для выполнения корутины

У `Dispatcher`'а есть несколько свойств:
- `Default` - используется по умолчанию (когда явно не указан никакой другой), например, для `launch()`
- `Main` - используется, когда нужно работать с потоком, который отвечает за UI
- `IO` - используется для задач ввода-вывода
- `Unconfined` - используется, когда не нужна привязка к какому-то конкретному потоку

В своём решении вместо `CoroutineScope` я использовал `lifecycleScope`. Его удобней использовать так как в нём есть suspend-функции, которые могут пригодиться при отслеживании жизненного цикла приложения.

Ключевая часть из [MainActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/3_continuewatch_(KotlinCoroutines)/app/src/main/java/ru/spbstu/icc/kspt/lab2/continuewatch/MainActivity.kt)
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    Log.d(TAG, "Activity onCreate(): created")

    lifecycleScope.launch(Dispatchers.Main) {
        whenResumed {
            while (true) {
                delay(1000)
                textSecondsElapsed.post {
                    textSecondsElapsed.setText("Seconds elapsed: " + secondsElapsed++)
                    threadsTV.setText("Number of threads: " + Thread.getAllStackTraces().keys.size)
                }
            }
        }
    }    
}
```
Запускаем корутину с помощью `lifecycleScope.launch()`, при этом явно указываем диспетчера `Dispatchers.Main`.

Далее используем suspend-функцию `whenResumed()`, которая помогает задетектить `Lifecycle.State.Resumed`. С помощью этого секундомер работает только когда приложение перешло в `onResume()`, а когда приложение свёрнуто - секундомер приостанавливается.

С помощью suspend-функции `delay()` мы приостанавливаем действие корутины, и что самое главное - не блокируем главный поток.

#### Демонстрация работы
Во всех трёх пунктах приложение работает одинаково<br>
![continuewatch_working.gif](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/screenshots/1%20continuewatch_working.gif)<br>
Рис. 1. Работа приложения

### Задача 2. Загрузка картинки в фоновом потоке (AsyncTask)
С помощью предоставленного кода со [StackOverflow](https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android/9288544#9288544) реализуем задачу.<br>

Ключевая часть из [LoadingImageActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/LoadImageActivity.kt)
```kotlin
class LoadImageActivity : AppCompatActivity() {

    private val urlForAsyncTask = "https://medialeaks.ru/wp-content/uploads/2020/06/laika-studios.jpg"

    ...

    override fun onCreate(savedInstanceState: Bundle?) {

        ...

        val loadButtonAsyncTask = findViewById<Button>(R.id.loadButton_AsyncTask)
        loadButtonAsyncTask.setOnClickListener {
            DownloadImageTask(findViewById(R.id.image_to_load)).execute(urlForAsyncTask)
        }

        ...

    }

    ...

    class DownloadImageTask(val bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg urls : String) : Bitmap? {
            val urldisplay = urls[0]
            var mIcon11 : Bitmap? = null
            try {
                val input = java.net.URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(input)
            } catch (e : Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result : Bitmap) {
            bmImage.setImageBitmap(result)
        }
    }
}
```

#### Результат
![asynctask.png](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/screenshots/2%20asynctask.png)<br>
Рис. 2. Это я сижу и очередной час дебажу неработающий код<br>

### Задача 3. Загрузка картинки в фоновом потоке (Kotlin Coroutines)
Немного перепишем код под корутины...<br>

Ключевая часть из [LoadingImageActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/LoadImageActivity.kt)
```kotlin
class LoadImageActivity : AppCompatActivity() {

    private val urlForAsyncTask = "https://medialeaks.ru/wp-content/uploads/2020/06/laika-studios.jpg"
    private val urlForCoroutines = "https://sun9-25.userapi.com/OUVWMtar9YKLzhD3nz_OHD1Y7jJyLPqXO39noA/-ONaP92RrhM.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        
        ...

        val loadButtonCoroutines = findViewById<Button>(R.id.loadButton_Coroutines)
        loadButtonCoroutines.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val image = loadImageForCoroutines(urlForCoroutines)
                launch(Dispatchers.Main) {
                    val bmImage = findViewById<ImageView>(R.id.image_to_load)
                    bmImage.setImageBitmap(image)
                }
            }
        }

        ...

    }

    private fun loadImageForCoroutines(vararg urls : String) : Bitmap? {
        val urldisplay = urls[0]
        var mIcon11 : Bitmap? = null
        try {
            val input = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(input)
        } catch (e : Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }
        return mIcon11
    }

    ...

}
```
Тут стоит обратить внимание на диспетчеры: для доступа в интернет и загрузки изображения используется `Dispatchers.IO`, затем, перелючаем работу на `Dispatchers.Main`, чтобы обратится к `ImageView` и поместить в неё загруженную картинку.

Переключать потоки нужно, так как IO-поток не может обратится к `View` и заменить его содержимое. Если не переключать поток, то вылетит `CalledFromWrongThreadException`:<br>
`android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.`

#### Результат
![coroutines.png](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/screenshots/3%20coroutines.png)<br>
Рис. 2. Это я на следующий день сижу и очередной час дебажу неработающий код<br>

#### P.S к Задаче 2 и Задаче 3
Мне показалось, что будет удобно объединить два решения в одном активити - `LoadImageActivity.kt`<br>
Поэтому вот<br>
![asynctask+coroutines.gif](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/screenshots/4%20asynctask%2Bcoroutines.gif)<br>

### Задача 4. Использование сторонних библиотек
Bitmap это конечно хорошо, но для таких частых задач, как асинхронная подгрузка изображения из интернета, можно использовать уже готовые решения в виде сторонних библиотек. Ведь вместо десятков строчек кода, можно написать ВСЕГО ОДНУ, Карл! И поможет нам в этом библиотека `Picasso`.<br>
[PicassoActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/PicassoActivity.kt)
```kotlin
class PicassoActivity : AppCompatActivity() {

    private val url = "https://sun9-32.userapi.com/impg/Z4-t3rPk-_iFbUB7m0UtEalGBUO89LaOzrurnw/bE6jbeWsmqw.jpg?size=604x403&quality=96&proxy=1&sign=aa92cca44ae28a8a42e1923454fd215a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Picasso"
        setContentView(R.layout.picasso_activity)

        val loadButton = findViewById<Button>(R.id.load_with_picasso_button)
        val imageView = findViewById<ImageView>(R.id.image_for_picasso)

        loadButton.setOnClickListener {
            Picasso.get().load(url).into(imageView)
        }
    }
}
```
#### Результат
![picasso.gif](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/screenshots/6%20picasso.gif)<br>

### Вывод
В данной работе мы познакомились с различными способами реализации многопоточности в Android.
- `Java Threads` - не самый лучший способ для андроида так как нужно самому следить на всеми созданными тредами и вовремя их удалять
- `AsyncTask` - устаревшее решение
- `Kotlin Coroutines` - на мой взгляд, самое лучшее из рассмотренных. Лучше, чем AsyncTask, так как позволяет писать меньше кода с тем же функционалом, да и еще делает его более понятным и лучше читаемым.

Также было доказано, что для стандартных задач, например "загрузка изображения из интернета" лучше использовать сторонние библиотеки, которые смогут выполнить задачу в пару строк кода, а не писать десятки строк самому.

### Приложение
- continuewatch
    - Kotlin-классы
        1. Java Threads: [MainActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/1_continuewatch_(JavaThreads)/app/src/main/java/ru/spbstu/icc/kspt/lab2/continuewatch/MainActivity.kt)
        2. AsyncTask: [MainActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/2_continuewatch_(AsyncTask)/app/src/main/java/ru/spbstu/icc/kspt/lab2/continuewatch/MainActivity.kt)
        3. Kotlin Coroutines: [MainActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/3_continuewatch_(KotlinCoroutines)/app/src/main/java/ru/spbstu/icc/kspt/lab2/continuewatch/MainActivity.kt)
    - XML
        1. [activity_main.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab6/3_continuewatch_(KotlinCoroutines)/app/src/main/res/layout/activity_main.xml)
    
- Подгрузка изображений из интернета
    1. AsyncTask+KotlinCoroutines
        - Activity: [LoadingImageActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/LoadImageActivity.kt)
        - XML: [load_image.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout/load_image.xml)
    2. Picasso library:
        - Activity: [PicassoActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/PicassoActivity.kt)
        - XML: [picasso_activity.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout/picasso_activity.xml)

