package ru.registrationbot.impl.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.registrationbot.RegistrationBot
import ru.registrationbot.api.converter.toClient
import ru.registrationbot.api.converter.toRecord
import ru.registrationbot.api.repository.ClientRepository
import ru.registrationbot.api.repository.HistoryRepository
import ru.registrationbot.api.service.ManagerService

@Service
class ManagerServiceImpl(
    private val clientRepository: ClientRepository,
    private val historyRepository: HistoryRepository
) : ManagerService {

    @Autowired
    lateinit var registrationBot: RegistrationBot

    @Transactional
    override fun getAllUsers() = registrationBot.sendRecord(clientRepository
        .findAll()
        .map { it.toClient().toString() })


    @Transactional
    override fun deleteUserInfo(idUser: Long) {
        clientRepository.deleteById(idUser.toInt())
    }

    @Transactional
    override fun getHistory(idUser: Long) = registrationBot.sendRecord(historyRepository
        .findByClient(idUser.toInt())
        .map { it.toRecord().toString() })
}
