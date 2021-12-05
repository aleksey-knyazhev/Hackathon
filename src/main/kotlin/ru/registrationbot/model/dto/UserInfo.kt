package ru.registrationbot.model.dto

import org.telegram.telegrambots.meta.api.objects.Message

class UserInfo(message: Message){
    val chatId: Long = message.chatId
    val phone: String? = if (message.contact != null) {
        message.contact.phoneNumber
    } else {
        null
    }
    val userName: String = message.chat.userName
    val firstName: String? = message.chat.firstName
    val lastName: String? = message.chat.lastName

}
