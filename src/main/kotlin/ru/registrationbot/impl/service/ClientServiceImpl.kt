package ru.registrationbot.impl.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.registrationbot.RegistrationBot
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
                        private val repositoryHistory: HistoryRepository,
                        private val serviceUtils: ServiceUtils
) : ClientService {

    @Autowired
    lateinit var registrationBot: RegistrationBot

    @Transactional
    override fun addRecording(idRecording: Long, user: UserInfo):DBServiceAnswer {
        var client = repositoryClient.findByChatId(user.chatId).orElse(null)
        if (client == null)
        {
            client = ClientsEntity(
                phone = user.phone,
                chatId = user.chatId,
                userName = user.userName,
                firstName = user.firstName,
                lastName = user.lastName)
            repositoryClient.save(client)
        }
        val record = repositoryTime.findById(idRecording).orElse(null)
        return if (record != null  && TimeslotStatus.FREE == record.status)
                {
                    record.status = TimeslotStatus.BOOKED
                    record.client = client.id

                    repositoryTime.save(record)
                    addHistory(client, record)

                    DBServiceAnswer.SUCCESS
                }
                else
                { DBServiceAnswer.FREE_RECORD_NOT_FOUND }
    }


    @Transactional
    override fun deleteRecording(idRecording: Long) {
        val record = repositoryTime.findById(idRecording).orElse(null)

        record.status = TimeslotStatus.BLOCKED
        val clientChatId = repositoryClient.findById(record.client!!).get().chatId
        record.client = null
        repositoryTime.save(record)
        registrationBot.sendCancelNotificationToClient(clientChatId, record.timeStart.toString())
    }

    override fun confirmRecording(userInfo: UserInfo) = changeStatusTimeSlot(userInfo, TimeslotStatus.CONFIRMED)

    override fun cancelRecording(userInfo: UserInfo) = changeStatusTimeSlot(userInfo, TimeslotStatus.FREE)

    private fun changeStatusTimeSlot(userInfo: UserInfo, status: TimeslotStatus): DBServiceAnswer {

        val client = repositoryClient.findByChatId(userInfo.chatId).orElse(null)

        if (client == null) {
            return DBServiceAnswer.CLIENT_NOT_FOUND
        }

        val record = repositoryTime.findByClient(client.id!!).orElse(null)
        if (record == null)
            return DBServiceAnswer.RECORD_NOT_FOUND

        record.status = status
        if (status == TimeslotStatus.FREE)
        {
            record.client = null
        }

        repositoryTime.save(record)

        addHistory(client, record)

        return DBServiceAnswer.SUCCESS
    }

    private fun addHistory(client: ClientsEntity, record: ScheduleEntity) {

        repositoryHistory.save(
            HistoryEntity(client = client.id!!, date = LocalDateTime.now(),
                action = record.status.name ,
                description = "${record.recordDate} c ${record.timeStart} до ${record.timeEnd}"))

    }

    override fun getBookedTimeWithClient(date: LocalDate): List<AutoNotificationDTO> {

        val forNotification = mutableListOf<AutoNotificationDTO>()

        val records = repositoryTime.findByStatusAndRecordDate(TimeslotStatus.BOOKED, date)
        val clients: Map<Int, ClientsEntity> = serviceUtils.getClientsMapByRecordsTime(records)

        for (record in records)
        {
            forNotification.add(AutoNotificationDTO(chatId = clients.get(record.client)!!.chatId,
                                                    recordDate = record.recordDate,
                                                    timeStart = record.timeStart,
                                                    timeEnd = record.timeEnd,
                                                    firstName =  clients.get(record.client)!!.firstName))
        }
        return forNotification
    }
}