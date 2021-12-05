package ru.registrationbot.impl.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.registrationbot.RegistrationBot
import ru.registrationbot.api.dto.AutoNotificationDTO
import ru.registrationbot.api.dto.TimeSlotDTO
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
import java.time.LocalTime
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
    override fun addRecording(date:LocalDate, time: LocalTime, user: UserInfo):DBServiceAnswer {
        var client = repositoryClient.findByChatId(user.chatId).orElse(null)
        if (client == null)
        {
            client = ClientsEntity(
                phone = user.phone,
                chatId = user.chatId,
                userName = user.userName,
                firstName = user.firstName,
                lastName = user.lastName)

            client = repositoryClient.save(client)
        }

        if (repositoryTime.findByClientAndRecordDate(client.id!!, date).isPresent)
            return DBServiceAnswer.RECORD_ALREADY_EXIST

        val record = repositoryTime.findByRecordDateAndTimeStart(date, time).orElse(null)
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
        val sendParameter = changeStatusTimeSlot(idRecording, TimeslotStatus.BLOCKED)
        registrationBot.sendCancelNotificationToClient(sendParameter.first, sendParameter.second)
    }

    @Transactional
    override fun cancelRecording(idRecording: Long) {
        val sendParameter = changeStatusTimeSlot(idRecording, TimeslotStatus.FREE)
    }

    private fun changeStatusTimeSlot(idRecording: Long, status: TimeslotStatus):Pair<Long, String>
    {
        val record = repositoryTime.findById(idRecording).orElse(null)

        record.status = status
        val clientChatId = repositoryClient.findById(record.client!!).get().chatId
        record.client = null
        repositoryTime.save(record)

        return Pair(clientChatId, record.timeStart.toString())
    }

    override fun confirmRecording(userInfo: UserInfo) = changeStatusTimeSlot(userInfo, TimeslotStatus.CONFIRMED)

    override fun cancelRecording(userInfo: UserInfo) = changeStatusTimeSlot(userInfo, TimeslotStatus.FREE)

    private fun changeStatusTimeSlot(userInfo: UserInfo, status: TimeslotStatus): DBServiceAnswer {

        val client = repositoryClient.findByChatId(userInfo.chatId).orElse(null)
            ?: return DBServiceAnswer.CLIENT_NOT_FOUND

        val record = repositoryTime.findByClient(client.id!!)
            .filter { it.recordDate == LocalDate.now().plusDays(1) }
            .singleOrNull()
            ?: return DBServiceAnswer.RECORD_NOT_FOUND

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

    override fun getClientWithActualRecords(userInfo: UserInfo): List<TimeSlotDTO> {

        val client = repositoryClient.findByChatId(userInfo.chatId).orElse(null)
        ?: return listOf()

        val forNotification = mutableListOf<TimeSlotDTO>()

        val records = repositoryTime.findByRecordDateAfterAndClient(LocalDate.now().minusDays(1L), client.id!!)

        for (record in records)
        {
            forNotification.add(TimeSlotDTO(idRecording = record.id!!,
                recordDate = record.recordDate,
                timeStart = record.timeStart,
                timeEnd = record.timeEnd,
                firstName =  client.firstName))
        }
        return forNotification
    }
}