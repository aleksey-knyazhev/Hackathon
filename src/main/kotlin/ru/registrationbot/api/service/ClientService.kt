package ru.registrationbot.api.service

import ru.registrationbot.api.dto.AutoNotificationDTO
import ru.registrationbot.api.dto.UserInfo
import ru.registrationbot.api.enums.DBServiceAnswer
import ru.registrationbot.impl.entities.ClientsEntity
import java.time.LocalDate

interface ClientService {

    /**
     * Метод для создания записи
     * idRecording - id записи в таблице расписаний
     * user - данные о клиенте
     */
    //true- успех, false- ошибка
    fun addRecording(idRecording: Long, user: UserInfo): DBServiceAnswer

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
     * Метод для получения списка забронированных слотов, chatId и имени клиента
     *  для конкретной даты (для автооповещения)
     */
    fun getBookedTimeWithClient(date: LocalDate):List<AutoNotificationDTO>

}