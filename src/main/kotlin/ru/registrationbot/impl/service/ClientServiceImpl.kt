package ru.registrationbot.impl.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.registrationbot.model.dto.AutoNotificationDTO
import ru.registrationbot.model.dto.TimeSlotDTO
import ru.registrationbot.api.service.ClientService
import ru.registrationbot.model.enums.DBServiceAnswer
import ru.registrationbot.model.dto.UserInfo
import ru.registrationbot.model.entities.ClientsEntity
import ru.registrationbot.model.enums.TimeslotStatus
import ru.registrationbot.api.repository.ClientRepository
import ru.registrationbot.api.repository.HistoryRepository
import ru.registrationbot.api.repository.ScheduleRepository
import ru.registrationbot.model.entities.HistoryEntity
import ru.registrationbot.model.entities.ScheduleEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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
    override fun deleteRecording(idRecording: Long):Boolean {
        val record = repositoryTime.findById(idRecording).orElse(null) ?: return false

        record.status = TimeslotStatus.BLOCKED
        val client = repositoryClient.findById(record.client!!).get()
        record.client = null
        repositoryTime.save(record)
        val text = "${client.firstName}, извините, Ваша запись на завтра в ${record.timeStart} отменена"
        registrationBot.sendNotificationToClient(client.chatId, text)
        return true
    }
    @Transactional
    override fun cancelRecording(idRecording: Long) {
        changeStatusTimeSlot(idRecording, TimeslotStatus.FREE)
    }

    private fun changeStatusTimeSlot(idRecording: Long, status: TimeslotStatus): DBServiceAnswer {
        val record = repositoryTime.findById(idRecording).orElse(null)

        record.status = status
        val clientUserName = repositoryClient.findById(record.client!!).get().userName
        record.client = null
        repositoryTime.save(record)
        var textToMng = "Клиент " +
                "@${clientUserName!!.replace("@","").replace("_", "\\_")} " +
                "отменил запись ${record.recordDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))} ${record.timeStart}-${record.timeEnd}"
        registrationBot.sendNotificationToMng(textToMng)
        return DBServiceAnswer.SUCCESS
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

        var textToMng = "Клиент " +
                "@${userInfo.userName.replace("@","").replace("_", "\\_")} " +
                "подтвердил запись на завтра в ${record.timeStart}"
        var textToClient = "Ваша запись на завтра в ${record.timeStart} подтверждена"

        record.status = status
        if (status == TimeslotStatus.FREE)
        {
            record.client = null
            textToMng = "Клиент " +
                    "@${userInfo.userName.replace("@","").replace("_", "\\_")} " +
                    "отменил запись на завтра в ${record.timeStart}"
            textToClient = "Ваша запись на завтра в ${record.timeStart} отменена"
        }

        repositoryTime.save(record)

        addHistory(client, record)

        registrationBot.sendNotificationToClient(userInfo.chatId, textToClient)
        registrationBot.sendNotificationToMng(textToMng)

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

    override fun getClientWithActualRecords(userInfo: UserInfo) {

        val client = repositoryClient.findByChatId(userInfo.chatId).orElse(null)
            ?: return

        val forNotification = mutableListOf<String>()

        val records = repositoryTime.findByRecordDateAfterAndClient(LocalDate.now().minusDays(1L), client.id!!)

        for (record in records)
        {
            forNotification.add(TimeSlotDTO(idRecording = record.id!!,
                recordDate = record.recordDate,
                timeStart = record.timeStart,
                timeEnd = record.timeEnd,
                firstName =  client.firstName).toString())
        }
        if (forNotification.isEmpty()) {
            registrationBot.sendNotificationToClient(userInfo.chatId,"Записей не найдено")
        } else {
            registrationBot.sendRecordToClient(userInfo.chatId, forNotification)
        }
    }
}