package ru.RegistrationBot.service

import java.time.LocalDate

interface TelegramBotService {

    /**
     * Метод для открытия записи
     * date - день для записи
     * time - временной диапазон (строка вида "10:00 18:00")
     */
    fun openNotification(date:LocalDate, time: String)

    /**
     * Метод для открытия записи
     * date - день для записи
     * time - время записи (строка вида "18:00")
     * chatId - Id чата с клиентом. По нему будут отправляться оповещения
     * login - логин клиента
     * firstName - имя клиента (при наличии)
     */
    fun addNotification(date: LocalDate, time: String, chatId: Long, login: String, firstName: String?)
}