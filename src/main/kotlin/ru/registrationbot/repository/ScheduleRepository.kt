package ru.registrationbot.repository

import org.springframework.data.repository.CrudRepository
import ru.registrationbot.entities.ScheduleEntity
import ru.registrationbot.enum.TimeslotStatus
import java.time.LocalDate

interface ScheduleRepository : CrudRepository<ScheduleEntity, Long> {

    fun findByStatus(status: TimeslotStatus): List<ScheduleEntity>

    fun findByStatusAndRecordDate(status: TimeslotStatus, date: LocalDate): List<ScheduleEntity>
}