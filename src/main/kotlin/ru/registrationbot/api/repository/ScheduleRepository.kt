package ru.registrationbot.api.repository

import org.springframework.data.repository.CrudRepository
import ru.registrationbot.impl.entities.ScheduleEntity
import ru.registrationbot.api.enums.TimeslotStatus
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

interface ScheduleRepository : CrudRepository<ScheduleEntity, Long> {

    fun findByStatus(status: TimeslotStatus): List<ScheduleEntity>

    fun findByStatusAndRecordDate(status: TimeslotStatus, date: LocalDate): List<ScheduleEntity>

    fun findByRecordDateAndTimeStart(date:LocalDate, time: LocalTime): Optional<ScheduleEntity>

    fun findByClient(clientId: Int): List<ScheduleEntity>

    fun findByRecordDateAfterAndClient(date: LocalDate, client: Int): List<ScheduleEntity>

}