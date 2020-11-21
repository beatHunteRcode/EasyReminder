# Лабораторная работа №4. RecyclerView
## Цели
- Ознакомиться с принципами работы adapter-based views.
- Получить практические навыки разработки адаптеров для view

### Задача 1. Знакомсотво с библиотекой biblib (unit test)
#### Принцип работы библиотеки
1. При обращении к элементу .bib-файла создаётся новый `BibEntry`
2. Библиотека имеет 2 режима работы: normal и strict. В strict mode работает искусственное ограничение: в памяти нельзя хранить более 20 записей одновременно. При извлечении 21-ой записи при помощи метода `assertDelegateIsValid()` 1-ая извлечённая запись становится невалидной (при доступе к полям кидаются исключения IllegalStateException)
3. При включенном `shuffle-mode` элементы `BibDatabase` перемешиваются

#### Тесты
##### Проверка strict-mode
1. По порядку вытаскиваем все элементы из `BibDatabase`
2. Так как при включенном *strict-mode* при обращении к `max-valid + 1'ому` элементу вылетает `IllegalStateException`, то для прохода теста, когда поймали ислючение - игнорируем его.
```
@Test
  public void strictModeThrowsException() {
    try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/forstrict.bib"))) {
      database = new BibDatabase(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }

    BibConfig cfg = database.getCfg();
    cfg.strict = true;

    BibEntry first = database.getEntry(0);
    for (int i = 0; i < cfg.maxValid - 1; i++) {
        BibEntry unused = database.getEntry(0);
        Assert.assertNotNull("Should not throw any exception @" + i, first.getType());
    }
    try {
        BibEntry unused = database.getEntry(0);
        first.getType();
        fail("Exception");
    }
    catch (IllegalStateException e) {
      //ignored
    }
  }
```
##### Проверка shuffle-mode
1. Создаем 1ую `BibDatabase` и берём из неё 1ый элемент - `shuffledDBEntry`
2. Проходим по циклу и в каждой итерации цикла создаем новую `BibDatabase` и сравниваем тип `shuffledDBEntry` с типом 1ого элемента из новосозданной `BibDatabase`
3. Если хотя бы 1 раз из 20 попыток типы не совпадают - перемешивание произошло, тест проходит.
```
@Test
  public void shuffleFlag() {
        BibConfig cfg = database.getCfg();
        cfg.shuffle = true;

        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/forshuffle.bib"))) {
            database = new BibDatabase(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BibEntry shuffledDBEntry = database.getEntry(0);

        boolean isShuffled = false;
        for (int i = 0; i < 20; i++) {
            try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/forshuffle.bib"))) {
                database = new BibDatabase(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (database.getEntry(0).getType() != shuffledDBEntry.getType()) {
                isShuffled = true;
            }
        }
        assertTrue(isShuffled);
  }
```
### Задача 2. Знакомство с RecyclerView. Неоднородный список.
#### 2.1 Лаба
Нужно отобразить элементы из .bib-файла [publication_ferro.bib](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/raw/publication_ferro.bib) в `RecyclerView`.<br>
Для начала нам нужна какая-то `Activity`, чтобы было с чего начать - [BibTexRVActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/BibTexRVActivity.kt)<br>
[bibtex_rv.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout-v21/bibtex_rv.xml)<br>
![BibTexRVActivity](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab4/screenshots/1%20BibTexRVActibity_xml.png)<br>
Рис. 1. BibTexRVActivity


Элементы из .bib-файла нужно где-то размещать. Создаём "шаблон", который будет использоваться при размещении каждого элемента на экране - [rem_list_item.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout/rem_list_item.xml)<br>
![rem_list_item](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab4/screenshots/2%20rem_list_item_xml.png)<br>
Рис. 2. rem_list_item

Чтобы размещать элементы .bib-файла на экране нужен **адаптер**<br>
Адаптер определяет на какой позиции будет находится элемент в списке и какое содержимое будет у этого элемента.
Создаём адаптер для нашего неоднородного списка - [BibTexAdapter.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Adapters/BibTexAdapter.kt)<br>
Внутри себя `BibTexAdapter` содержит вложенный класс `BibTexViewHolder`, который определяет какое наполнение будет у элементов списка в `RecyclerView`
```
class BibTexViewHolder(itemView : View, parentContext: Context) : RecyclerView.ViewHolder(itemView) {
        var pagesTV : TextView = itemView.findViewById(R.id.tv_date)
        var yearTV : TextView = itemView.findViewById(R.id.tv_time)
        var titleTV : TextView = itemView.findViewById(R.id.tv_text)

        var context = parentContext

        var bg : FrameLayout = itemView.findViewById(R.id.rem_frame)

        fun bind(titleText : String, pagesText : String, yearText : String, type: Types) {
            titleTV.text = titleText
            pagesTV.text = pagesText
            yearTV.text = yearText

            if (type == Types.ARTICLE) bg.background = context.getDrawable(R.color.waters)
            if (type == Types.BOOK) bg.background = context.getDrawable(R.color.avocado)
            if (type == Types.BOOKLET) bg.background = context.getDrawable(R.color.cyberpunkBackground1)
            if (type == Types.CONFERENCE) bg.background = context.getDrawable(R.color.bee)
            if (type == Types.INBOOK) bg.background = context.getDrawable(R.color.bloody_red)
            if (type == Types.INCOLLECTION) bg.background = context.getDrawable(R.color.pinkie)
            if (type == Types.INPROCEEDINGS) bg.background = context.getDrawable(R.color.orange)
            if (type == Types.MANUAL) bg.background = context.getDrawable(R.color.purple)
            if (type == Types.MASTERSTHESIS) bg.background = context.getDrawable(R.color.swamp)
            if (type == Types.MISC) bg.background = context.getDrawable(R.color.yellow)
            if (type == Types.PHDTHESIS) bg.background = context.getDrawable(R.color.toxic)
            if (type == Types.PROCEEDINGS) bg.background = context.getDrawable(R.color.colorAccent)
            if (type == Types.TECHREPORT) bg.background = context.getDrawable(R.color.colorPrimaryDark)
            if (type == Types.UNPUBLISHED) bg.background = context.getDrawable(R.color.colorPrimary)

        }
    }
```
Для вывода я вытаскивал 3 поля их каждого элемента .bib-файла
- название (`Keys.TITLE`)
- год (`Keys.YEAR`)
- количество страниц (`Keys.PAGES`)

В результате совмещения всего получилось что-то такое<br>
![bib_rv](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab4/screenshots/3%20bib_rv.png)<br>
Рис. 3. Вывод элементов .bib-файла на экран в RecyclerView<br>
По заданию нужно было как-то отделить элементы одного типа от другого, для каждого типа я выбрал свой цвет, который определяется при размещении элемента в `RecyclerView` в методе `BibTexViewHolder.bind()`
```
fun bind(titleText : String, pagesText : String, yearText : String, type: Types) {
            titleTV.text = titleText
            pagesTV.text = pagesText
            yearTV.text = yearText

            if (type == Types.ARTICLE) bg.background = context.getDrawable(R.color.waters)
            if (type == Types.BOOK) bg.background = context.getDrawable(R.color.avocado)
            if (type == Types.BOOKLET) bg.background = context.getDrawable(R.color.cyberpunkBackground1)
            if (type == Types.CONFERENCE) bg.background = context.getDrawable(R.color.bee)
            if (type == Types.INBOOK) bg.background = context.getDrawable(R.color.bloody_red)
            if (type == Types.INCOLLECTION) bg.background = context.getDrawable(R.color.pinkie)
            if (type == Types.INPROCEEDINGS) bg.background = context.getDrawable(R.color.orange)
            if (type == Types.MANUAL) bg.background = context.getDrawable(R.color.purple)
            if (type == Types.MASTERSTHESIS) bg.background = context.getDrawable(R.color.swamp)
            if (type == Types.MISC) bg.background = context.getDrawable(R.color.yellow)
            if (type == Types.PHDTHESIS) bg.background = context.getDrawable(R.color.toxic)
            if (type == Types.PROCEEDINGS) bg.background = context.getDrawable(R.color.colorAccent)
            if (type == Types.TECHREPORT) bg.background = context.getDrawable(R.color.colorPrimaryDark)
            if (type == Types.UNPUBLISHED) bg.background = context.getDrawable(R.color.colorPrimary)

        }
```
<br>

#### 2.2 Свой проект
Не обращайте внимания на то, что текст из элементов .bib-файла в прошлом пункте не влезает на экран и всё в принципе выглядит вырвиглазно. Так как я делаю своё приложение, то "шаблон" я делал под свои цели - размещение первых слов напоминания.
В своё приложение я тоже решил внедрить `RecyclerView`. Он находится в разделе *Мои напоминания/My Reminders*<br>
![MyReminders с RecyclerView](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab4/screenshots/4%20my_project.png)<br>
Рис. 4. MyReminders с RecyclerView<br>
Вон та зеленая круглая кнопочка, обведенная оранжевым, нужна для перехода на экран демонстрации `RecyclerView` для .bib-файла. Позже можно будет изменить её функционал на "добавление напоминания", чтобы для добавления напоминания не нужно было переходить на главный экран.

### Задача 3. Бесконечный список.
Для реализации бесконечного списка понадобилось изменить метод `BibTexAdapter.getItemCount()`. Теперь он возвращает не количество элементов в файле, я оооочень больше число.<br>
```
override fun getItemCount(): Int {
    return Int.MAX_VALUE
}
```
Для условия "после последнего элемента все записи повторяются, начиная с первой" я обнуляю локальный счётчик `i` (в методе `BibTexAdapter.onBindViewHolder()`), когда тот достигнет числа элементов в .bib-файле.
```
if (i < numbElements - 1) i++
else i = 0
```
### Выводы
В данной работе мы
- ознакомились с принципами работы adapter-based views
- получили практические навыки разработки адаптеров для view - реализовали `RecyclerView` с адаптером `BibTexAdapter`

### Приложение
- Всё для .bib-файла
    - [BibTexRVActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/BibTexRVActivity.kt)
    - [BibTexAdapter.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Adapters/BibTexAdapter.kt)
    - [bibtex_rv.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout-v21/bibtex_rv.xml)
- Свой проект
    - [MyRemindersActivity.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Activities/MyRemindersActivity.kt)
    - [RemindersAdapter.kt](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/java/com/beathunter/easyreminder/Adapters/RemindersAdapter.kt)
    - [my_reminders.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout/my_reminders.xml)
    - [rem_list_item.xml](https://github.com/beatHunteRbbx/EasyReminder/blob/master/app/src/main/res/layout/rem_list_item.xml)