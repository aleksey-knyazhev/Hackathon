package ru.registrationbot.api.dto

import java.time.LocalDateTime

class RecordDto(
    var id: Int?,
    var client: Int?,
    var date: LocalDateTime?,
    var action: String?,
    var description: String?
)