package ru.registrationbot.api.dto

class ClientDto(
    val idRecording: Long?,
    val phone: String?,
    val chatId: Long?,
    val userName: String?,
    val firstName: String?,
    val lastName: String?
) {
    override fun toString(): String {
        return "$idRecording ${firstName.orEmpty()} " +
                "${lastName.orEmpty()} @${userName.orEmpty().replace("@","")} ${phone.orEmpty()}"
    }
}