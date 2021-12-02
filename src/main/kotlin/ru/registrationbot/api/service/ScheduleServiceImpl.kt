package ru.registrationbot.api.service


import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.registrationbot.entities.ScheduleEntity
import ru.registrationbot.enum.TimeslotStatus
import ru.registrationbot.repository.ScheduleRepository
import java.time.LocalDate
import java.time.LocalTime


@Service
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository
): SchedulerService {

    @Transactional
    override fun openRecording(date: LocalDate, time: String) {
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
            currentStartTime =  currentStartTime.plusHours(1)
        }
    }

    override fun getDates(): List<LocalDate> {
        TODO("Not yet implemented")
    }

    override fun getTimesForDate(date: LocalDate): List<String> {
        TODO("Not yet implemented")
    }

}