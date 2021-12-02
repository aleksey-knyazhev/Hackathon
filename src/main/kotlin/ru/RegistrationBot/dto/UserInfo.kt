package ru.RegistrationBot.dto

import org.telegram.telegrambots.meta.api.objects.Message

class UserInfo(message: Message){
    val chatId: Long = message.chatId
    val userName: String = message.chat.userName
    val firstName: String? =  message.chat.firstName
}
