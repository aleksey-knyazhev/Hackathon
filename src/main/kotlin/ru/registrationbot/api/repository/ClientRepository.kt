package ru.registrationbot.api.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.registrationbot.impl.entities.ClientsEntity
import java.util.*

@Repository
interface ClientRepository: CrudRepository<ClientsEntity, Long> {

    fun findByChatId(chatId: Long): Optional<ClientsEntity>

}