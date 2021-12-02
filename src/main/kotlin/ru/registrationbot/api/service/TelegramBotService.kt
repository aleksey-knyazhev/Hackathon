package ru.registrationbot.api.service

import java.time.LocalDate

interface TelegramBotService {

    /**
     * Метод для создания
     * date - день для записи
     * time - время записи (строка вида "18:00")
     * chatId - Id чата с клиентом. По нему будут отправляться оповещения
     * login - логин клиента
     * firstName - имя клиента (при наличии)
     */
    @Deprecated(message = "использовать метод addRecording()")
    fun addNotification(date: LocalDate, time: String, chatId: Long, login: String, firstName: String?)


}