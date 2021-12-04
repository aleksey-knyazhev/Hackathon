package ru.registrationbot.api.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.registrationbot.api.enums.TimeslotStatus
import ru.registrationbot.impl.entities.ClientsEntity
import java.time.LocalDate
import java.util.*

@Repository
interface ClientRepository: CrudRepository<ClientsEntity, Long> {

    fun findByChatId(chatId: Long): Optional<ClientsEntity>

    fun deleteById(id: Int)

    fun findByIdIn(id: Set<Int>): List<ClientsEntity>

}