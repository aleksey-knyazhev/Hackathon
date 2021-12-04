package ru.registrationbot.impl.service

import org.springframework.stereotype.Service
import ru.registrationbot.api.enums.TimeslotStatus
import ru.registrationbot.api.repository.ClientRepository
import ru.registrationbot.api.repository.ScheduleRepository
import ru.registrationbot.api.service.ReportService
import java.time.LocalDate

@Service
class ReportServiceImpl(
    private val clientRepository: ClientRepository
): ReportService {

    override fun getUnconfirmedRecording(): List<String> {
        val date = LocalDate.now().plusDays(1)
        val report = clientRepository.findByDateAndStatus(date, TimeslotStatus.BOOKED)

        return listOf(String())
    }

    override fun getConfirmedRecording(): List<String> {
        val date = LocalDate.now().plusDays(1)
        val report = clientRepository.findByDateAndStatus(date, TimeslotStatus.CONFIRMED)

        return listOf(String())
    }
}