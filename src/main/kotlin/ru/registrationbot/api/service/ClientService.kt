package ru.registrationbot.api.service

import ru.registrationbot.model.dto.AutoNotificationDTO
import ru.registrationbot.model.dto.UserInfo
import ru.registrationbot.model.enums.DBServiceAnswer
import java.time.LocalDate
import java.time.LocalTime

interface ClientService {

    /**
     * Метод для создания записи
     * idRecording - id записи в таблице расписаний
     * user - данные о клиенте
     */
    //true- успех, false- ошибка
    fun addRecording(date:LocalDate, time: LocalTime, user: UserInfo): DBServiceAnswer

    /**
     * Метод для удаления записи
     * idRecording - id записи
     * при удалении записи нужно оповестить клиента с помощью метода sendCancelNotification() класса RegistrationBot
     */
    //Возвращается chatId, если запись была найдена или null, чтобы можно было отправить сообщение клиенту
    fun deleteRecording(idRecording: Long) : Boolean


    /**
     * Метод для подтверждения записи
     * userInfo - информация о клиенте
     * Ищем запись на завтрашний день для указанного пользователя и проставляем соответствующий статус
     */
    //
    fun confirmRecording(userInfo: UserInfo): DBServiceAnswer

    /**
     * Метод для удаления записи
     * userInfo - информация о клиенте
     * Ищем запись на завтрашний день для указанного пользователя и освобождаем ее
     */
    fun cancelRecording(userInfo: UserInfo): DBServiceAnswer

    /**
     * Метод для удаления записи
     * idRecording - id таймслота клиента
     * Ищем запись на завтрашний день для указанного id слота и освобождаем ее
     */
    fun cancelRecording(idRecording: Long)

    /**
     * Метод для получения списка забронированных слотов, chatId и имени клиента
     *  для конкретной даты (для автооповещения)
     */
    fun getBookedTimeWithClient(date: LocalDate):List<AutoNotificationDTO>

    /**
     * Метод для получения списка записей клиента на даты, начиная с текущей
     */
    fun getClientWithActualRecords(userInfo: UserInfo)
}