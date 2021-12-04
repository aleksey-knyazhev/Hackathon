package ru.registrationbot.api.dto

import java.time.LocalDate
import java.time.LocalTime

class AutoNotificationDTO(val chatId: Long,
                          var recordDate: LocalDate,
                          var timeStart: LocalTime,
                          var timeEnd: LocalTime
                          )