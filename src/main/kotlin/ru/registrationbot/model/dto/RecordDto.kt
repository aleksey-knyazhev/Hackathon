package ru.registrationbot.model.dto

import java.time.LocalDateTime

class RecordDto(
    var id: Int?,
    var client: Int?,
    var date: LocalDateTime?,
    var action: String?,
    var description: String?
) {
    override fun toString(): String {
        return "$date ${action.orEmpty()} ${description.orEmpty()}"
    }
}
