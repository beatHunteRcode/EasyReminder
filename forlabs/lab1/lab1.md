# Лабораторная работа №1. Layouts

## Цели
- Ознакомиться со средой разработки Android Studio
- Изучить основные принципы верстки layout с использованием View и ViewGroup
- Изучить основные возможности и свойства LinearLayout
- Изучить основные возможности и свойства ConstraintLayout

## Ход работы
1. Создайте layout ресурсы с использованием **LinearLayout**.<br>

    **LinearLayout** выравнивает все дочерние элементы вертикально/горизонтально<br>
    - вертикально `android:orientation="vertical"`
    - горизонтально `android:orientation="horizontal"`

    ![Рис. 1 Сцена с использованием LinearLayout](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab1/screenshots/1.png)<br>
    Рис. 1 Сцена с использованием LinearLayout


2.	Создайте layout ресурсы с использованием **ConstraintLayout**.

    **ConstraintLayout** – более сложное представление виджетов. **ConstraintLayout** позволяет более гибко настраивать дочерние элементы (не только вертикально/горизинтально). Для правильной настройки, дочернему элементу необходимо установить ограничения по вертикали и горизонтали (хотя бы по одному) – **constraints**.

    ![Рис. 2 Сцена с использованием ConstraintLayout](https://github.com/beatHunteRbbx/EasyReminder/blob/master/forlabs/lab1/screenshots/2.png)<br>
    Рис. 2 Сцена с использованием ConstraintLayout


## Вывод

В результате работы изучены основы верстки layout. 
**LinearLayout** – строгое представление виджетов. Используется, когда нужно строго и ровно разместить элементы по вертикали/горизонтали.
**ConstraintLayout** – более гибкое представление элементов. Используется, когда нужна сложная верстка, прикрепление элементов друг к другу или родителям.

## Приложение

### activity.main

```
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"

    tools:context=".MainActivity">

    <ImageView
        android:id="@+id/bg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:srcCompat="@drawable/bg" />

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="153dp"
        android:layout_marginTop="20dp"
        android:layout_marginEnd="152dp"
        android:text="Nearest"
        android:textAlignment="center"
        android:textColor="#C1C1C1"
        android:textSize="30sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="@+id/bg"
        app:layout_constraintStart_toStartOf="@+id/bg"
        app:layout_constraintTop_toTopOf="@+id/bg" />

    <HorizontalScrollView
        android:id="@+id/ScrollView_mainscreen"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginVertical="10dp"
        android:scrollbars="none"
        app:layout_constraintBottom_toTopOf="@+id/add_rem_button"
        app:layout_constraintEnd_toEndOf="@+id/bg"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="@+id/bg"
        app:layout_constraintTop_toBottomOf="@+id/textView">

        <LinearLayout
            android:id="@+id/hLinLayout"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:orientation="horizontal">
        </LinearLayout>
    </HorizontalScrollView>

    <Button
        android:id="@+id/add_rem_button"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:background="@drawable/add_rem_button"
        android:onClick="onAddRemButtonClick"
        app:layout_constraintBottom_toBottomOf="@+id/bg"
        app:layout_constraintEnd_toEndOf="@+id/bg"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="@+id/bg"
        app:layout_constraintTop_toTopOf="@+id/bg"
        app:layout_constraintVertical_bias="0.559" />

    <Button
        android:id="@+id/my_rems_button"
        android:layout_margin="20dp"
        android:layout_marginStart="20dp"
        android:layout_marginEnd="20dp"
        android:background="@drawable/buttonshape"
        android:onClick="onMyRemsButtonClick"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintDimensionRatio="5:1"
        app:layout_constraintEnd_toEndOf="@+id/bg"
        app:layout_constraintStart_toStartOf="@+id/bg"
        app:layout_constraintTop_toBottomOf="@+id/add_rem_button"
        app:layout_constraintVertical_bias="0.20"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textColor="#000000"
        android:textSize="30sp"
        android:layout_marginTop="30dp"
        android:layout_weight="0"
        android:layout_gravity="center"
        android:text="My reminders"
        android:shadowColor="#A8A8A8"
        android:shadowDx="0"
        android:shadowDy="0"
        android:shadowRadius="5" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### adding_reminder.xml
```
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/add_rem_main_constr_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/bg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:adjustViewBounds="true"
        android:scaleType="centerCrop"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="1.0"
        app:srcCompat="@drawable/bg" />

    <LinearLayout
        android:id="@+id/lin1"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:paddingHorizontal="60dp"
        android:paddingVertical="20dp">

        <EditText
            android:id="@+id/reminding_text"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:layout_weight="10"
            android:ems="10"
            android:inputType="textPersonName"
            android:text="write your reminding here"
            android:textAlignment="center" />

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


</androidx.constraintlayout.widget.ConstraintLayout>
```