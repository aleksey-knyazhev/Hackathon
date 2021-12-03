package ru.registrationbot.impl.service

import org.springframework.stereotype.Service
import ru.registrationbot.api.service.ClientService
import ru.registrationbot.api.enums.DBServiceAnswer
import ru.registrationbot.api.dto.UserInfo
import ru.registrationbot.impl.entities.ClientsEntity
import ru.registrationbot.api.enums.TimeslotStatus
import ru.registrationbot.api.repository.ClientRepository
import ru.registrationbot.api.repository.HistoryRepository
import ru.registrationbot.api.repository.ScheduleRepository
import ru.registrationbot.impl.entities.HistoryEntity
import ru.registrationbot.impl.entities.ScheduleEntity
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class ClientServiceImpl(private val repositoryTime: ScheduleRepository,
                        private val repositoryClient: ClientRepository,
                        private val repositoryHistory: HistoryRepository
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
                    val timeSlot = record.get()
                    timeSlot.status = TimeslotStatus.BOOKED
                    timeSlot.client = clientId

                    repositoryTime.save(timeSlot)
                    addHistory(clientId!!, timeSlot)

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

        val timeSlot = record.get()
        timeSlot.status = status
        if (status == TimeslotStatus.FREE)
        {
            timeSlot.client = null
        }
        repositoryTime.save(timeSlot)

        addHistory(client.get().id!!, timeSlot)

        return DBServiceAnswer.SUCCESS
    }

    private fun addHistory(idClient: Int, timeSlot: ScheduleEntity) {

        repositoryHistory.save(
            HistoryEntity(client = idClient, date = LocalDateTime.now(),
                action = timeSlot.status.name ,
                description = "${timeSlot.recordDate} c ${timeSlot.timeStart} до ${timeSlot.timeEnd}"))

    }
}