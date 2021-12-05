package ru.registrationbot.api.dto

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeSlotDTO(var idRecording: Long,
                  var recordDate: LocalDate,
                  var timeStart: LocalTime,
                  var timeEnd: LocalTime,
                  var firstName: String?
) {
    override fun toString(): String {
        return "$idRecording\t\t${recordDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))}\t$timeStart-$timeEnd"
    }
}