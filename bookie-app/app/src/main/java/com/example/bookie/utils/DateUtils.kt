package com.example.bookie.utils

import java.util.*

object DateUtils {

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    fun getDifference(startDate: Date, endDate: Date): String {

        //milliseconds
        var difference = endDate.time - startDate.time

        val minutesInMilli = 60000
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = difference / daysInMilli
        difference %= daysInMilli

        val elapsedHours = difference / hoursInMilli
        difference %= hoursInMilli

        val elapsedMinutes = difference / minutesInMilli
        difference %= minutesInMilli

        if (elapsedDays >=1 ){
            return "$elapsedDays days ago"
        } else if (elapsedHours >= 1) {
            return "$elapsedHours hours ago"
        }else if (elapsedMinutes >= 1) {
            return "$elapsedMinutes minutes ago"
        } else {
            return "Just now"
        }
    }

}