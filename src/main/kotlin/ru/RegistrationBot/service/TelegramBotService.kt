package ru.RegistrationBot.service

import ru.RegistrationBot.dto.UserInfo
import java.time.LocalDate

interface TelegramBotService {

    /**
     * Метод для открытия записи
     * date - день для записи
     * time - временной диапазон (строка вида "10:00 18:00")
     */
    fun openRecording(date:LocalDate, time: String)

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

    /**
     * Метод для получения дат, на которые открыта запись
     */
    fun getDates():List<LocalDate>

    /**
     * Метод для получения свободных слотов времени для конкретной даты
     * Список должен состоять из строк, содержащих id записи и время, разделенных пробелом (257 10:00)
     */
    fun getTimesForDate(date: LocalDate):List<String>

    /**
     * Метод для получения списка неподтвержденных записей на завтрашний день
     * Список должен состоять из строк, содержащих id записи, времени записи, имя и логин клиента
     * К логину клиента необходимо добавлять префикс @
     */
    fun getUnconfirmedRecording():List<String>

    /**
     * Метод для получения списка подтвержденных записей на завтрашний день
     * Список должен состоять из строк, содержащих id записи, времени записи, имя и логин клиента
     * К логину клиента необходимо добавлять префикс @
     */
    fun getConfirmedRecording():List<String>

    /**
     * Метод для создания записи
     * idRecording - id записи
     * user - данные о клиенте
     */
    fun addRecording(idRecording: Long, user: UserInfo)

    /**
     * Метод для удаления записи
     * idRecording - id записи
     */
    fun deleteRecording(idRecording: Long)

    /**
     * Метод для получения списка всех пользователей
     * idRecording - id записи
     * в качестве элементов списка можно возвращать например UserInfo, но обязательно нужно передавать на фронт id пользователя
     */
    fun getAllUsers():List<String>

    /**
     * Метод для удаления пользователя
     * idUser - id клиента
     */
    fun deleteUserInfo(idUser: Long)

    /**
     * Метод для получения истории по клиенту
     * idUser - id клиента
     */
    fun getHistory(idUser: Long):List<String>

    /**
     * Метод для подтверждения записи
     * userInfo - информация о клиенте
     * Ищем запись на завтрашний день для указанного пользователя и проставляем соответствующий статус
     */
    fun confirmRecording(userInfo: UserInfo)

    /**
     * Метод для удаления записи
     * userInfo - информация о клиенте
     * Ищем запись на завтрашний день для указанного пользователя и освобождаем ее
     */
    fun cancelRecording(userInfo: UserInfo)
}