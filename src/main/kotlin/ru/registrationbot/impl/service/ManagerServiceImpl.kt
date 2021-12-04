package ru.registrationbot.impl.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    @Transactional
    override fun getAllUsers() = clientRepository
        .findAll()
        .map { it.toClient() }

    @Transactional
    override fun deleteUserInfo(idUser: Long) {
        clientRepository.deleteById(idUser.toInt())
    }

    @Transactional
    override fun getHistory(idUser: Long) = historyRepository
        .findByClient(idUser.toInt())
        .map { it.toRecord() }
}
