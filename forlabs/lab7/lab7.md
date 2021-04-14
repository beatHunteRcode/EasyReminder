# Лабораторная работа №7. Сервисы и Broadcast Receivers.

## Цели 
Получить практические навыки разработки сервисов (started и bound) и Broadcast Receivers.

## Задачи

### Задача 1. Started сервис для скачивания изображения
Для начала создадим *Activity* в котором будем запускать сервис - `ImageLoadServiseActivity.kt`

![ImageLoadServiseActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab7/screenshots/1.png)<br>
Рис. 1 ImageLoadServiseActivity.kt<br>

При нажатии на кнопку *Donwload* запускается сервис по скачиванию изображения из интернета по URL

Ключевая часть из `ImageLoadServiseActivity.kt`
```kotlin
val downloadBtn = findViewById<Button>(R.id.service_start_btn)
downloadBtn.setOnClickListener {
    val serviceIntent = Intent(this, ImageLoadService::class.java)
    serviceIntent.putExtra("imageURL", urlForCoroutines);
    startService(serviceIntent)  
}
```

А вот и сам сервис

ImageLoadServise.kt
```kotlin
class ImageLoadService : Service() {

    val BROADCAST_ACTION = "com.beathunter.easyreminder.action.settext"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val urlForCoroutines = intent?.getStringExtra("imageURL")

        CoroutineScope(Dispatchers.IO).launch {
            val image = loadImageForCoroutines(urlForCoroutines!!)
            val path = saveFile(image!!)
            val downloadedImageIntent = Intent()
            downloadedImageIntent.putExtra("image_path", path)
            downloadedImageIntent.action = BROADCAST_ACTION
            sendBroadcast(downloadedImageIntent)
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    private fun saveFile(image : Bitmap) : String {
        val fileContainer = getOutputMediaFile()
        val fos = FileOutputStream(fileContainer)
        image.compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()

        return fileContainer.absolutePath
    }

    //взял со StackOverFlow
    private fun getOutputMediaFile() : File {
        //получаем путь до файлового хранилища
        val storage = File(filesDir.absolutePath)
        //создаём директорию, если её не существует
        if (!storage.exists()) storage.mkdir()
        val fileName = "image.png"
        return File("${storage}/${fileName}")
    }

    private fun loadImageForCoroutines(vararg urls : String) : Bitmap? {
        Log.d("Broadcastyyy", Thread.currentThread().name)
        val urldisplay = urls[0]
        var mIcon11 : Bitmap? = null
        try {
            val input = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(input)
        } catch (e : Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }
        Log.d("Broadcastyyy", "Image has been downloaded")
        return mIcon11
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}
```

При запуске сервиса выполняется метод `onStartCommand()`

`onStartCommand()`
```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val urlForCoroutines = intent?.getStringExtra("imageURL")

        CoroutineScope(Dispatchers.IO).launch {
            val image = loadImageForCoroutines(urlForCoroutines!!)
            val path = saveFile(image!!)
            val downloadedImageIntent = Intent()
            downloadedImageIntent.putExtra("image_path", path)
            downloadedImageIntent.action = BROADCAST_ACTION
            sendBroadcast(downloadedImageIntent)
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }
```
В нём с помощью `loadImageForCoroutines()` загружаем картинку из интернета<br>
Потом с помощью `saveFile()` сохраняем её во внутремннем хранилище устройства<br>
Затем помещаем путь до файла в `Intent` с помощью `downloadedImageIntent.putExtra("image_path", path)`, ставим action для будущего интент-фильтра и отправляем *broadcast*-сообщение с путем до файла с помощью `sendBroadcast()` и останавлиаем сервис, так как он больше не нужен.<br>

Скачивание изображения выполняется не в UI потоке, в доказательство к этому можно посмотреть логи
```
2021-01-13 09:41:11.750 13975-13975/com.example.lab7_broadcastreceiver D/Broadcastyyy: null
2021-01-13 09:42:33.829 14189-14216/com.beathunter.easyreminder D/Broadcastyyy: DefaultDispatcher-worker-1
2021-01-13 09:42:34.041 14189-14216/com.beathunter.easyreminder D/Broadcastyyy: Image has been downloaded
2021-01-13 09:42:34.151 13975-13975/com.example.lab7_broadcastreceiver D/Broadcastyyy: Receiver has got a path
2021-01-13 09:42:41.524 13975-13975/com.example.lab7_broadcastreceiver D/Broadcastyyy: /data/user/0/com.beathunter.easyreminder/files/image.png
```
Также в подтверждение тому, что картинка качается не в UI потоке можно сказать, что приложение успешно работает и мы не получаем `NetworkOnMainThreadException`. Если не использовать `Dispatchers.IO`, то в методе `loadImageForCoroutines()` мы будем получать `NetworkOnMainThreadException`.

### Задача 2. Broadcast Receiver
Я написал новое приложение-"болванку" для `BroadcastReceiver'а`

Ключева часть из `Lab7_broadcastreceiver/MainActivity.kt`
```kotlin
val BROADCAST_ACTION = "com.beathunter.easyreminder.action.settext"
var ir = ImageReceiver()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val filter = IntentFilter(BROADCAST_ACTION)
    registerReceiver(ir, filter)
    
    Log.d("Broadcastyyy", intent?.getStringExtra("path")?:"null")
    val textView = findViewById<TextView>(R.id.textView_forpath)
    textView.setText(intent?.getStringExtra("path")?:"null")
}
```
В методе `onCreate()` я создал `IntentFilter`, чтобы "отфильтровать" *broadcast*-сообщение с интентом из прошлого пункта. Затем регистрирую пару `BroadcastReceiver`+`IntentFilter`
```kotlin
val filter = IntentFilter(BROADCAST_ACTION)
registerReceiver(ir, filter)
```

Теперь посмотрим на Inner-класс в `MainActivity` - `ImageReceiver`, который наследуется от `BroadcastReceiver`<br>

Ключевая часть из `Lab7_broadcastreceiver/MainActivity.kt`: Inner-класс - `ImageReceiver`
```kotlin
class ImageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val path = intent?.getStringExtra("image_path")
        Log.d("Broadcastyyy", "Receiver has got a path")
        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.putExtra("path", path)
        context?.startActivity(activityIntent)
    }
}
```
В нём мы достаем путь, который отправили в *broadcast*-сообщении в прошлом пункте, из интента
```kotlin
val path = intent?.getStringExtra("image_path")
```
Затем создаём новый интент и помещаем в него полученный путь, чтобы передать его в активити приложения-"болванки"
```kotlin
val activityIntent = Intent(context, MainActivity::class.java)
activityIntent.putExtra("path", path)
```

Вся работа выглядит следующим образом:
1. запускаем `BroadcastReceiver` - приложение-болванку
2. переключаемся на основное приложение и в нём нажимает на кнопку **Download** - скачиваем картинку и отправляем broadcast-запрос
3. переключаемся на приложение-болванку, которое ловит broadcast-сообщение и получает из него путь к файлу
4. смотрим на `TextView`, который теперь должен отображать путь к файлу, скачанному из основного приложения

![2.gif](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab7/screenshots/2.gif)


### Задача 3. Bound Service для скачивания изображения

Обновим наше активити. Добавим кнопки
- BIND SEVICE - для привязки клиента к сервису
- SEND URL - для отправки URL-картинки сервису
- SHOW PATH - для отображения присланного серисом пути в TextView

Действия для кнопок

`ImageLoadServiceActivity.kt`
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
        
        ...

        val bindServiceBtn = findViewById<Button>(R.id.bind_service_button)
        bindServiceBtn.setOnClickListener {
            val serviceIntent = Intent(this, ImageLoadService::class.java)
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        }

        val sendURLButton = findViewById<Button>(R.id.send_url_button)
        sendURLButton.setOnClickListener {
            sendURL()
        }

        val showPathBtn = findViewById<Button>(R.id.show_path_button)
        showPathBtn.setOnClickListener {
            val pathTV = findViewById<TextView>(R.id.path_tv)
            pathTV.setText(pathText)
        }
}
```

![3.png](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab7/screenshots/3.png)

#### 3.1.1 BIND SERVICE - ACTIVITY
**НАЖИМАЕМ НА BIND SERVICE**<br>
Обратим внимание на метод `bindService()` в кнопке `bindServiceBtn`
```
 bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
```
- `serviceIntent` - интент сервиса, с которым будем связывать клиент
- `connection` - объект типа `ServiceConnection
- `Context.BIND_AUTO_CREATE` - флаг, означающий, что, если сервис, к которому мы пытаемся подключиться, не работает, то он будет запущен.

`connection`
```kotlin
val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("Broadcastyyy", "Connected to service")
            mService = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("Broadcastyyy", "Disconnected from service")
            isBound = false
        }

}
```
#### 3.1.2 BIND SERVICE - SERVICE
Теперь мы подключились к сервису. После того, как выполняется метод `bindService()` в самом сервисе выполняется метод `onBind()`<br>
`ImageLoadService.kt/onBind()`
```kotlin
override fun onBind(intent: Intent?): IBinder? {
        Log.d("Broadcastyyy", "Binding to service")
        return Messenger(IncomingHandler(this)).binder
}
```
Если `onBind()` возвращает `null` - это started-сервис
У нас `onBind()` возвращает `IBinder`, который передаётся в объект типа `ServiceConnection` и помещается во 2ой аргумент метода `onServiceConnected()` для того, чтобы мы могли получить ссылку на сервис, к которому привязались, для активити

*возвращаемся в активити, к `connection`*<br>
`ImageLoadServiceActivity.kt/connection`
```kotlin
var mService : Messenger? = null

...

val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("Broadcastyyy", "Connected to service")
            mService = Messenger(service)
            isBound = true
        }

        ...
}
```
Вот и весь процесс привязки к сервису

#### 3.2.1 SEND URL - ACTIVITY
**НАЖИМАЕМ НА SEND URL**<br>
`ImageLoadServiceActivity.kt/onCreate()` & `ImageLoadServiceActivity.kt/sendURL()`
```kotlin
private val urlForCoroutines = "https://sun9-25.userapi.com/OUVWMtar9YKLzhD3nz_OHD1Y7jJyLPqXO39noA/-ONaP92RrhM.jpg"
companion object {
        val MSG_OUTCOMING_LOCATION = 2
        ...
}
var isBound : Boolean = false
var mService : Messenger? = null
...

override fun onCreate(savedInstanceState: Bundle?) {

        ...

        val sendURLButton = findViewById<Button>(R.id.send_url_button)
        sendURLButton.setOnClickListener {
            sendURL()
        }

        ...

}

private fun sendURL() {
    if (!isBound) return
    val responseMessenger = Messenger(OutcomingHandler(this))
    val message : Message = Message.obtain().apply {
        what = ImageLoadService.MSG_INCOMING_URL
        obj = urlForCoroutines
        replyTo = responseMessenger
    }
    mService?.send(message)
}
```
В методе `sendURL()` формируем сообщение. В процессе формирования заполняем разные поля
- `what` - тип сообщения
- `obj` - объект типа Object. В нашем случае - URL картинки
- `replyTo` - тот, кому мы будем "отвечать" - возвращать путь к скачанной картинке. Заметим, что в объект `Messenger` передаётся объект кастомного класса `OutcomingHandler`, который наследуется от `Handler`. Пока не будем подробно его разбирать. Он понадобиться нам позднее, в самом конце.

 И отправляем сервису через `mService?.send(message)`
#### 3.2.1 SEND URL - SERVICE
Сервису нужно сообщение как-то поймать. В этом ему поможет кастомный класс `IncomingHandler`, наследующийся от `Handler`<br>
`ImageLoadService.kt/IncomingHandler`
```kotlin
inner class IncomingHandler() : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_INCOMING_URL -> {
                    Log.d("Broadcastyyy", "Message received. Received URL: ${msg.obj}")
                    val url = msg.obj.toString()
                    val responseMessenger = msg.replyTo
                    CoroutineScope(Dispatchers.IO).launch {
                        val image = loadImageForCoroutines(url)
                        val path = saveFile(image!!)
                        val message = Message.obtain(null, MSG_OUTCOMING_LOCATION, path)
                        responseMessenger.send(message)
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }
```
В этом классе переопределенный метод `handleMessage()` ловит присланное сообщение, вытаскивает из поля `message.obj` присланный URL-картинки и скачивает её, используя корутины. Затем, используя `saveFile()` получает путь к картике, составляет ответное сообщение и отправляет его обратно в активити.<br>
Методы `loadImageForCoroutines()` и `saveFile()` взяты из прошлых пунктов.

К разговору о потоках...<br>
И опять же, как и в прошлом пункте, в подтверждение тому, что код скачивается картики происходит не в UI-потоке служит `Dispacherts.IO`. Если не использовать `Dispatchers.IO`, то в методе `loadImageForCoroutines()` мы будем получать `NetworkOnMainThreadException`.

#### 3.2.3 SEND URL - ОПЯТЬ ACTIVITY
Возвращаемся в активити, чтобы поймать ответное сообщение от сервиса с путём к картинке.<br>
В этом нам, как раз, поможет вышеупомянутый `OutcomingHandler`, наследующийся от `Handler`

`ImageLoadServiceActivity.kt/OutcomingHandler`
```kotlin
companion object {
        ...
        var pathText : String = "null"
}

class OutcomingHandler() : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_OUTCOMING_LOCATION -> {
                    Log.d("Broadcastyyy", "Answer received. Received path: ${msg.obj}")
                    pathText = msg.obj.toString()
                }
                else -> super.handleMessage(msg)
            }
        }
    }
```
В этом классе переопределенный метод `handleMessage()`, как и в `IncomingHandler`, ловит присланное сообщение, вытаскивает из поля `message.obj` присланный путь к картинке и помещает его в переменную `pathText`

#### 3.3 SHOW PATH
Тут ничего сверхестестенного. Просто помещаем, полученный из сервиса, путь к картинке в `TextView`
```kotlin
    companion object {
            ...
            var pathText : String = "null"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        
        ...

        val showPathBtn = findViewById<Button>(R.id.show_path_button)
        showPathBtn.setOnClickListener {
            val pathTV = findViewById<TextView>(R.id.path_tv)
            pathTV.setText(pathText)
        }
    }
```

А вот итоговая работа<br>
![4.gif](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab7/screenshots/4.gif)


Логи
```
D/Broadcastyyy: Binding to service
D/Broadcastyyy: Connected to service
D/Broadcastyyy: Message received. Received URL: https://sun9-25.userapi.com/OUVWMtar9YKLzhD3nz_OHD1Y7jJyLPqXO39noA/-ONaP92RrhM.jpg
D/Broadcastyyy: DefaultDispatcher-worker-2
D/Broadcastyyy: Image has been downloaded
D/Broadcastyyy: Answer received. Received path: /data/user/0/com.beathunter.easyreminder/files/image.png
```
## Выводы
В этой работе мы познакомились с сервисами "запустили-забыли" (started-сервисы) и сервисами, к которым можно приконнектится (bound-сервисы)
Если мы работаем со started-сервисом, то для "общения" сервиса с клиентами используются broadcast-сообщения
Если мы работает с bound-сервисами, то у них можно использовать двухсторонний механизм "общения" - `Message(Handler)`

## Приложение
- Классы
    - [ImageLoadServiceActivity.kt](https://github.com/beatHunteRcode/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/ImageLoadServiceActivity.kt)
    - [ImageLoadService.kt](https://github.com/beatHunteRcode/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Services/ImageLoadService.kt)
    - [MainActivity.kt](https://github.com/beatHunteRcode/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/MainActivity.kt)
- XML
    - [service_activity.xml](https://github.com/beatHunteRcode/EasyReminder/blob/master/app/src/main/res/layout/service_activity.xml)
    - [activity_main.xml](https://github.com/beatHunteRcode/EasyReminder/blob/master/app/src/main/res/layout/activity_main.xml) (broadcast-receiver aka приложение-"болванка")