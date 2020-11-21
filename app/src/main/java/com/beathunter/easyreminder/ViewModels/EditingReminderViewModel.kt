package com.beathunter.easyreminder.ViewModels

import androidx.lifecycle.ViewModel

class EditingReminderViewModel : ViewModel() {

    companion object {
        private var dateButtonText: String = "set date"
        private var timeButtonText: String = "set time"
        private var remindingText: String = ""

        public fun getDateButtonText(): String { return this.dateButtonText }
        public fun getTimeButtonText(): String { return this.timeButtonText }
        public fun getRemindingText(): String { return this.remindingText }

        public fun setDateButtonText(dateButtonText: String) { this.dateButtonText = dateButtonText }
        public fun setTimeButtonText(timeButtonText: String) { this.timeButtonText = timeButtonText }
        public fun setRemindingText(remindingText: String) { this.remindingText = remindingText }
    }
}