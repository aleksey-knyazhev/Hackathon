package ru.registrationbot.impl.service

import org.springframework.stereotype.Service
import ru.registrationbot.api.service.ClientService
import ru.registrationbot.api.enums.DBServiceAnswer
import ru.registrationbot.api.dto.UserInfo
import ru.registrationbot.impl.entities.ClientsEntity
import ru.registrationbot.api.enums.TimeslotStatus
import ru.registrationbot.api.repository.ClientRepository
import ru.registrationbot.api.repository.ScheduleRepository
import javax.transaction.Transactional

@Service
class ClientServiceImpl(private val repositoryTime: ScheduleRepository,
                        private val repositoryClient: ClientRepository
) : ClientService {

    @Transactional
    override fun addRecording(idRecording: Long, user: UserInfo):DBServiceAnswer {
        val client = repositoryClient.findByChatId(user.chatId)
        val clientId =
                    if (!client.isPresent)
                    {
                        val newClient = ClientsEntity(
                            phone = user.phone,
                            chatId = user.chatId,
                            userName = user.userName,
                            firstName = user.firstName,
                            lastName = user.lastName)
                        repositoryClient.save(newClient).id!!
                    }
                    else
                    {client.get().id}

        val record = repositoryTime.findById(idRecording)
        return if (record.isPresent && TimeslotStatus.FREE == record.get().status)
                {
                    record.get().status = TimeslotStatus.BOOKED
                    record.get().client = clientId

                    repositoryTime.save(record.get())
                    DBServiceAnswer.SUCCESS
                }
                else
                { DBServiceAnswer.FREE_RECORD_NOT_FOUND }
    }


    @Transactional
    override fun deleteRecording(idRecording: Long):Int? {
        val record = repositoryTime.findById(idRecording)

        if (!record.isPresent ) //TODO нужно ли проверять статус на занятость слота?
        {
            return null
        }

        record.get().status = TimeslotStatus.BLOCKED
        val clientChatId = record.get().client
        record.get().client = null
        repositoryTime.save(record.get())
        return clientChatId
    }

    override fun confirmRecording(userInfo: UserInfo) = changeStatusTimeSlot(userInfo, TimeslotStatus.CONFIRMED)

    override fun cancelRecording(userInfo: UserInfo) = changeStatusTimeSlot(userInfo, TimeslotStatus.FREE)

    private fun changeStatusTimeSlot(userInfo: UserInfo, status: TimeslotStatus): DBServiceAnswer {

        val client = repositoryClient.findByChatId(userInfo.chatId)

        if (!client.isPresent) {
            return DBServiceAnswer.CLIENT_NOT_FOUND
        }

        val record = repositoryTime.findByClient(client.get().id!!)
        if (!record.isPresent)
            return DBServiceAnswer.RECORD_NOT_FOUND

        record.get().status = status
        if (status == TimeslotStatus.FREE)
        {
            record.get().client = null
        }
        repositoryTime.save(record.get())

        return DBServiceAnswer.SUCCESS
    }
}