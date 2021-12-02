package ru.registrationbot.service

import ru.registrationbot.dto.UserInfo

interface ClientService {

    /**
     * Метод для создания записи
     * idRecording - id записи
     * user - данные о клиенте
     */
    fun addRecording(idRecording: Long, user: UserInfo)

    /**
     * Метод для удаления записи
     * idRecording - id записи
     * при удалении записи нужно оповестить клиента с помощью метода sendCancelNotification() класса RegistrationBot
     */
    fun deleteRecording(idRecording: Long)


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