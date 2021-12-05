package ru.registrationbot.impl.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.registrationbot.RegistrationBot
import ru.registrationbot.api.enums.TimeslotStatus
import ru.registrationbot.api.repository.ScheduleRepository
import ru.registrationbot.api.service.ReportService
import ru.registrationbot.impl.entities.ClientsEntity
import java.time.LocalDate

@Service
class ReportServiceImpl(
    private val repositoryTime: ScheduleRepository,
    private val serviceUtils: ServiceUtils
): ReportService {

    @Autowired
    lateinit var registrationBot: RegistrationBot

    override fun getUnconfirmedRecording() = getNotificationReportByStatus(TimeslotStatus.BOOKED)

    override fun getConfirmedRecording() = getNotificationReportByStatus(TimeslotStatus.CONFIRMED)

    private fun getNotificationReportByStatus(status: TimeslotStatus)  {
        val date = LocalDate.now().plusDays(1)
        //записи из расписния
        val records = repositoryTime.findByStatusAndRecordDate(status, date)
        //  мапа клиентов: id клиента -> объект клиента
        val clients: Map<Int, ClientsEntity> = serviceUtils.getClientsMapByRecordsTime(records)
        val result = mutableListOf<String>()

        for(record in records)
        {
            val client = clients.get(record.client)!!
            result.add("${record.id} ${record.recordDate} ${client.firstName.orEmpty()} " +
                    "@${client.userName.orEmpty().replace("@","").replace("_", "\\_")} " +
                    client.phone.orEmpty()
            )
        }
        registrationBot.sendRecordToMng(result)
    }
}