package ru.registrationbot.impl.service

import org.springframework.stereotype.Service
import ru.registrationbot.api.enums.TimeslotStatus
import ru.registrationbot.api.repository.ClientRepository
import ru.registrationbot.api.repository.ScheduleRepository
import ru.registrationbot.api.service.ReportService
import ru.registrationbot.impl.entities.ClientsEntity
import java.time.LocalDate

@Service
class ReportServiceImpl(
    private val repositoryTime: ScheduleRepository,
    private val serviceUtils: ServiceUtils
): ReportService {

    override fun getUnconfirmedRecording(): List<String> {
        val date = LocalDate.now().plusDays(1)
//записи из расписния
        val records = repositoryTime.findByStatusAndRecordDate(TimeslotStatus.BOOKED, date)
//  мапа клиентов: id клиента -> объект клиента
        val clients: Map<Int, ClientsEntity> = serviceUtils.getClientsMapByRecordsTime(records)

        return listOf(String())
    }

    override fun getConfirmedRecording(): List<String> {
        val date = LocalDate.now().plusDays(1)
//записи из расписния
        val records = repositoryTime.findByStatusAndRecordDate(TimeslotStatus.BOOKED, date)
//  мапа клиентов: id клиента -> объект клиента
        val clients: Map<Int, ClientsEntity> = serviceUtils.getClientsMapByRecordsTime(records)

        return listOf(String())
    }
}