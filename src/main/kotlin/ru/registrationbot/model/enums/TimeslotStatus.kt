package ru.registrationbot.model.enums

enum class TimeslotStatus(
    val description: String
) {
    FREE("Свободно"),
    BOOKED("Запись не подтверждена"),
    CONFIRMED("Запись подтверждена"),
    BLOCKED("Запись заблокирована")
}