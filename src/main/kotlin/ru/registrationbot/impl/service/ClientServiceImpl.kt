package ru.registrationbot.impl.service

import org.checkerframework.checker.nullness.Opt.isPresent
import org.springframework.stereotype.Service
import ru.registrationbot.api.dto.AutoNotificationDTO
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
import java.time.LocalDate
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
                    addHistory(client.get(), record.get())

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

       val record = client.get().scheduleEntity
            .stream().filter{it.id == 1L}.findFirst()

        if (isPresent(record) && record.get().status == TimeslotStatus.FREE)
        {
            record.get().client = null
        }

        record.get().status = status
        repositoryClient.save(client.get())

        addHistory(client.get(), record.get())

        return DBServiceAnswer.SUCCESS
    }

    private fun addHistory(client: ClientsEntity, record: ScheduleEntity) {

        repositoryHistory.save(
            HistoryEntity(client = client.id!!, date = LocalDateTime.now(),
                action = record.status.name ,
                description = "${record.recordDate} c ${record.timeStart} до ${record.timeEnd}"))

    }

    override fun getBookedTimeWithClient(date: LocalDate): List<AutoNotificationDTO> {
        return listOf()
        //repositoryClient.findByDateAndStatus(date, TimeslotStatus.BOOKED)
    }
}