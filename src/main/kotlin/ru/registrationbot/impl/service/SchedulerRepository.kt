package ru.registrationbot.impl.service

import org.springframework.data.repository.CrudRepository
import ru.registrationbot.entities.ClientsEntity
import ru.registrationbot.entities.ScheduleEntity
import java.util.*

interface SchedulerRepository: CrudRepository<ScheduleEntity, Long> {

    fun findByClient(clientId: Long): Optional<ScheduleEntity>

}