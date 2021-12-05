package ru.registrationbot.api.service

interface ManagerService {

    /**
     * Метод для получения списка всех пользователей
     * idRecording - id записи
     * в качестве элементов списка можно возвращать например UserInfo, но обязательно нужно передавать на фронт id пользователя
     */
    fun getAllUsers()

    /**
     * Метод для удаления пользователя
     * idUser - id клиента
     * при удалении пользователя нужно отправить ему уведомление с помощью метода sendDeleteNotification() класса RegistrationBot
     */
    fun deleteUserInfo(idUser: Long)


    /**
     * Метод для получения истории по клиенту
     * idUser - id клиента
     */
    fun getHistory(idUser: Long)

}