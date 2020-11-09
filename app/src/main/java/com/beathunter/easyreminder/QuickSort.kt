package com.beathunter.easyreminder

class QuickSort {
    companion object {
        public fun quickSort(array: ArrayList<Reminder>, from: Int, to: Int) {
            if (from < to) {
                val divideIndex: Int = partition(array, from, to)

                quickSort(array, from, divideIndex - 1)  //сортируем левый подмассив
                quickSort(array, divideIndex, to)           //сортируем правый подмассив
            }
        }

        private fun partition(array: ArrayList<Reminder>, from: Int, to: Int): Int {
            var rightIndex = to
            var leftIndex = from

            val pivot = array[from].millis
            while (leftIndex <= rightIndex) {
                while (array[leftIndex].millis < pivot) leftIndex++
                while (array[rightIndex].millis > pivot) rightIndex--
                if (leftIndex <= rightIndex) {
                    swap(array, rightIndex, leftIndex)
                    leftIndex++
                    rightIndex--
                }
            }
            return leftIndex
        }

        private fun swap(array: ArrayList<Reminder>, index1: Int, index2: Int) {
            val temp = array[index1]
            array[index1] = array[index2]
            array[index2] = temp
        }
    }
}