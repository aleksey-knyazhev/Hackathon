package ru.registrationbot.repository

import org.springframework.data.repository.CrudRepository
import ru.registrationbot.entities.ScheduleEntity

interface ScheduleRepository : CrudRepository<ScheduleEntity, Long> {
}