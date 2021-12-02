package ru.registrationbot.impl.service


import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.registrationbot.api.service.SchedulerService
import ru.registrationbot.entities.ScheduleEntity
import ru.registrationbot.enum.TimeslotStatus
import ru.registrationbot.repository.ScheduleRepository
import java.time.LocalDate
import java.time.LocalTime


@Service
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository
) : SchedulerService {

    @Transactional
    override fun openRecording(date: LocalDate, time: String) {
        val regex = Regex("\\d{2}:\\d{2} \\d{2}:\\d{2}")
        if (!regex.matches(time)) {
            throw Exception("Неверный формат времени \"10:00 18:00\"")
        } else if (date < LocalDate.now()) {
            throw Exception("Дата прошла")
        }
        val twoDates = time.split(" ")
        var currentStartTime = LocalTime.parse(twoDates.first())
        val shiftEnd = LocalTime.parse(twoDates.last())
        while (shiftEnd.minusHours(1L) >= currentStartTime) {
            scheduleRepository.save(
                ScheduleEntity().apply {
                    recordDate = date
                    timeStart = currentStartTime
                    timeEnd = currentStartTime.plusHours(1L)
                    status = TimeslotStatus.FREE
                }
            )
            currentStartTime = currentStartTime.plusHours(1)
        }
    }

    override fun getDates() = scheduleRepository
        .findByStatus(TimeslotStatus.FREE)
        .filter { it.recordDate >= LocalDate.now() }
        .map { it.recordDate }.distinct().sorted()

    override fun getTimesForDate(date: LocalDate) = scheduleRepository
        .findByStatusAndRecordDate(TimeslotStatus.FREE, date)
        .filter { it.timeStart > LocalTime.now() }
        .map { "${it.timeStart} ${it.timeEnd}" }.sorted()
}