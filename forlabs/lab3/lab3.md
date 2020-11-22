## Лабораторная работа №3. Lifecycle компоненты. Навигация в приложении.

### Цели
- Ознакомиться с методом обработки жизненного цикла activity/fragment при помощи Lifecycle-Aware компонентов
- Изучить основные возможности навигации внутри приложения: созданиие новых activity, navigation graph

### 1. Обработка жизненного цикла с помощью Lifecycle-Aware компонентов

Изучены **Lifecycle-Aware** компоненты. Проделаны задания из [codelabs](https://developer.android.com/topic/libraries/architecture/lifecycle#codelabs).<br> 
В результате работы получены новые знания:
- **ViewModel** - новый способ сохранения состояния **Activity**
- **LifecycleObserver** - интерфейс, который можно использовать при реализации класса, который имеет жизненный цикл в приложении - **Android Lifecycle**
- **Subscribe to lifecycle events** - способ управления действиями **Activity**. Действия зависят от состояний **Lifecycle** (**ON_RESUME**, **ON_PAUSE**, **ON_STOP**, etc). Для реализации этого способа необходимо добавить в **наблюдатели** (observer) кастомный класс, который реализует интерфейс **LifecycleObserver**. Затем у метода, который мы хотим вызывать при изменении состояния **Activity**, написать аннотацию<br>
`@OnLifecycleEvent(Lifecycle.Event.<ON_RESUME/ON_PAUSE/ON_STOP/etc)`
- **SavedStateHandle** - класс, который помогает сохранять и восстонавливать состояния отдельных **View** в **Activity** даже если процесс приложения завершился

### 2. Навигация (startActivityForResult)
Приложение начинает работу с главного экрана - *MainActivity.kt*<br>
![Рис. 1 Главный экран приложения](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab3/screenshots/1.png)<br>
Рис. 1 Главный экран приложения

Затем по нажатию на "плюсик" в центре экрана **Activity** меняется на другое - *AddingReminderActivity.kt*
![Рис. 2 Экран добавления напоминальщика](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab3/screenshots/2.png)<br>
Рис. 2 Экран добавления напоминальщика

Реализованы это с помощью класса **Intent**
```
val addRemButton = findViewById<Button>(R.id.add_rem_button)
        addRemButton.setOnClickListener {
            val intent: Intent = Intent(this, AddingReminderActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_REM)
        }
```

Метод `startActivityForResult()` меняет **Activity** на *AddingReminderActivity.kt* и ожидает ответа от этого **Activity**

Ответ отсылается с помощью метода `onCreateButtonClick()`
```
fun onCreateButtonClick(v : View) {
        val intent: Intent = Intent()
        intent.putExtra("dateText", addingReminderViewModel.getDateButtonText())
        intent.putExtra("timeText", addingReminderViewModel.getTimeButtonText())
        addingReminderViewModel.setRemindingText(findViewById<TextView>(R.id.reminding_text).text.toString())
        intent.putExtra("remText", addingReminderViewModel.getRemindingText())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
```
И принимается в *MainActivity.kt* с помощью метода `onActivityResult()`
```
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) return

        val dateText = data.getStringExtra("dateText")
        val timeText = data.getStringExtra("timeText")
        val remText = data.getStringExtra("remText")

        createNearestReminding(dateText!!, timeText!!, remText!!)
    }
```
![Рис. 3 Переход между Activity](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab3/screenshots/3.jpg)<br>
Рис. 3 Переход между Activity

P.S. Прежде чем собираться переключать **Activity** с помощью **Intent**, каждую **Activity** необходимо зарегистрировать в файле *AndroidManifest.xml*. Если этого не сделать, то можно получить `ActivityNotFoundException`.


### 3. Навигация (флаги Intent/атрибуты Activity)
Также сделаны переходы между разными **Activity** с помощью **Intent Flags**
```
addRemButton.setOnClickListener {
            val intent: Intent = Intent(this, AddingReminderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivityForResult(intent, REQUEST_CODE_ADD_REM)
        }
```
```
val myRemsButton = findViewById<Button>(R.id.my_rems_button)
        myRemsButton.setOnClickListener {
            val intent: Intent = Intent(this, MyRemindersActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
```
В этих примерах используются два Intent флага
- `FLAG_ACTIVITY_SINGLE_TOP` - реализует *launchMode = singleTop*
- `FLAG_ACTIVITY_NEW_TASK` - реализует *launchMode = singleTask*

### 4. Навигация (Fragments, Navigation Graph)

**Navigation Graph** - тип XML-ресурсов, который определяет все возможные пути между **Activity**, по которым пользователь может перемещаться.

Так как я не нашел необходимости внедрить Navigation Graph в свой проект, то я сделал "болванку", чтобы продемонстрировать возможнотсти перехода между фрагментами с помощью Navigation Graph

![Рис. 4 Схема Navigation Graph](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab3/screenshots/4.png)<br>
Рис. 4 Схема Navigation Graph

Реализованы переходы между фрагментами по схеме графа. Переходы осуществлялись с помощью *navigation action*

![Рис. 5 Переход между Fragments](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab3/screenshots/5.png)<br>
Рис. 5 Переход между Fragments

Пример настройки перехода из Фрагмента1 к Фрагменту2 для кнопки *TO 2*
```
val view = inflater.inflate(R.layout.fragment_blank, container, false)

view.to2btn.setOnClickListener {
    Navigation.findNavController(it).navigate(R.id.action_blankFragment_to_blankFragment2)        
}
```

Был рассмотрен пример перехода из Фрагмента3 к Фрагменту2 через navigation action не относящийся к Фрагменту3 
```
val view = inflater.inflate(R.layout.fragment_blank3, container, false)
        
view.tryto2btn.setOnClickListener {
    Navigation.findNavController(it).navigate(R.id.action_blankFragment_to_blankFragment2)
}
```
В результате чего при нажатии на кнопку *TRY TO 2* получен `IllegalArgumentException`
```
java.lang.IllegalArgumentException: navigation destination com.beathunter.easyreminder:id/action_blankFragment_to_blankFragment2 is unknown to this NavController
```

### Выводы
В данной работе мы
- ознакомились с методом обработки жизненного цикла activity/fragment при помощи Lifecycle-Aware компонентов
- ознакомились с переходами между `Activity` с помощью `Intent`, `startActivity()` и `startActivityForResult()`
- изучили основные возможности навигации внутри приложения: созданиие новых activity, navigation graph, переходы между фрагментами


### Ссылки на файлы, используемые в данной лабораторной
- Activity
    - [MainActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/MainActivity.kt)
    - [AddingReminderActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/AddingReminderActivity.kt)
- Фрагменты для Navigation Graph
    - [Kotlin-классы](https://github.com/beatHunteRbbx/EasyReminder/tree/master/app/src/main/java/com/beathunter/easyreminder/Fragments)
    - XML-файлы
        - [fragment_blank.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout/fragment_blank.xml)
        - [fragment_blank2.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout/fragment_blank2.xml)
        - [fragment_blank3.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout/fragment_blank3.xml)
- [XML-файл Navigation Graph'a](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/navigation/navigation.xml)