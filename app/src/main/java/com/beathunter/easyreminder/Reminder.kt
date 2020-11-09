package com.beathunter.easyreminder

import java.text.SimpleDateFormat
import java.util.*

class Reminder(text : String, date: String, time : String) {

    public var text : String = text
    public var date : String = date
    public var time : String = time

    /**
     * millis - переменная отвечающая за значение Date.time
     * нужна для сортировки напоминаний по датам для дальнейшего вывода их на экран приложения
     * в порядке возрастания дат
     */
    public var millis : Long


    init {
        val datePattern = "dd.MM.yyyy K:mm a"
        val dateFormat = SimpleDateFormat(datePattern, Locale.ENGLISH)
        val dateToParse = this.date + " " + this.time
        val date = dateFormat.parse(dateToParse)
        millis = date!!.time
    }

    /**
     * Compares this date with other (incoming) date
     *
     * @return 1 - if this date is greater then other date
     * @return -1 - if this date is lesser then other date
     * @return 0 - if this date is equal to other date
     * */
    fun compareTo(other: Any?) : Int {
        val datePattern = "dd.MM.yyyy K:mm a"
        val dateFormat = SimpleDateFormat(datePattern, Locale.ENGLISH)

        val thisDateToParse = date + time
        val thisDate = dateFormat.parse(thisDateToParse)

        other as Reminder
        val otherDateToParse = other.date + other.time
        val otherDate = dateFormat.parse(otherDateToParse)

        return when {
            thisDate!!.time > otherDate!!.time -> 1
            thisDate.time < otherDate.time -> -1
            else -> 0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reminder

        if (text != other.text) return false
        if (date != other.date) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }
}