package ru.registrationbot.model.dto

class ClientDto(
    val idRecording: Long?,
    val phone: String?,
    val chatId: Long?,
    val userName: String?,
    val firstName: String?,
    val lastName: String?
) {
    override fun toString(): String {
        return "${firstName.orEmpty()} " +
                "${lastName.orEmpty()} " +
                "@${userName.orEmpty().replace("@","").replace("_", "\\_")} ${phone.orEmpty()}\n" +
                "Посмотреть историю клиента: /history\\_$idRecording\n" +
                "Удалить данные и записи клиента: /delete\\_$idRecording"
    }
}