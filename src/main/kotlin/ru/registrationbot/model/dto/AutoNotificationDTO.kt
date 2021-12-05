package ru.registrationbot.model.dto

import java.time.LocalDate
import java.time.LocalTime

class AutoNotificationDTO(var chatId: Long,
                          var recordDate: LocalDate,
                          var timeStart: LocalTime,
                          var timeEnd: LocalTime,
                          var firstName: String?
                          )