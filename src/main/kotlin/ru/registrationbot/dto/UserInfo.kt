package ru.registrationbot.dto

import org.telegram.telegrambots.meta.api.objects.Message

class UserInfo(message: Message){
    val chatId: Long = message.chatId
    val phone: String? = message.contact.phoneNumber
    val userName: String = message.chat.userName
    val firstName: String? =  message.chat.firstName
    val lastName: String? = message.chat.lastName
//todo телефоны нужно проверить те ли?

}
