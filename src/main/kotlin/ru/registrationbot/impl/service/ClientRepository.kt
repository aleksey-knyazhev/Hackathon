package ru.registrationbot.impl.service

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.registrationbot.entities.ClientsEntity
import java.util.*

@Repository
interface ClientRepository: CrudRepository<ClientsEntity, Long> {

    fun findByChatId(chatId: Long): Optional<ClientsEntity>

}