package ru.registrationbot.impl.service

import org.springframework.stereotype.Service
import ru.registrationbot.api.service.ClientService
import ru.registrationbot.dto.UserInfo

@Service
class ClientServiceImpl: ClientService {

    override fun addRecording(idRecording: Long, user: UserInfo) {
        TODO("Not yet implemented")
    }

    override fun deleteRecording(idRecording: Long) {
        TODO("Not yet implemented")
    }

    override fun confirmRecording(userInfo: UserInfo) {
        TODO("Not yet implemented")
    }

    override fun cancelRecording(userInfo: UserInfo) {
        TODO("Not yet implemented")
    }
}