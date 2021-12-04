package ru.registrationbot.impl.service

import org.springframework.stereotype.Service
import ru.registrationbot.api.repository.ClientRepository
import ru.registrationbot.impl.entities.ScheduleEntity
import kotlin.streams.toList

@Service
class ServiceUtils(private val repositoryClient: ClientRepository) {
   fun getClientsMapByRecordsTime(records : List<ScheduleEntity>) =
       repositoryClient.findByIdIn(records.stream().filter{it.client != null}.map{it.client!!}
        .toList().toSet())
        .groupBy(keySelector = {it.id!!} , valueTransform = {it})
        .mapValues{it.value.first()}
}