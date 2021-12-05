package ru.registrationbot.impl.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.registrationbot.RegistrationBot
import ru.registrationbot.api.converter.toClient
import ru.registrationbot.api.converter.toRecord
import ru.registrationbot.api.enums.TimeslotStatus
import ru.registrationbot.api.repository.ClientRepository
import ru.registrationbot.api.repository.HistoryRepository
import ru.registrationbot.api.repository.ScheduleRepository
import ru.registrationbot.api.service.ManagerService
import ru.registrationbot.impl.entities.ScheduleEntity
import java.time.LocalDateTime

@Service
class ManagerServiceImpl(
    private val clientRepository: ClientRepository,
    private val historyRepository: HistoryRepository,
    private val scheduleRepository: ScheduleRepository
) : ManagerService {

    @Autowired
    lateinit var registrationBot: RegistrationBot

    @Transactional
    override fun getAllUsers() = registrationBot.sendRecordToMng(clientRepository
        .findAll()
        .map { it.toClient().toString() })


    @Transactional
    override fun deleteUserInfo(idUser: Long) {
        scheduleRepository.findByClient(idUser.toInt())
            .filter { LocalDateTime.of(it.recordDate, it.timeStart) > LocalDateTime.now() }
            .forEach {
                it.client = null
                it.status = TimeslotStatus.FREE
                scheduleRepository.save(it)
            }
        var userChatId = clientRepository.findById(idUser.toInt()).get().chatId
        clientRepository.deleteById(idUser.toInt())
        registrationBot.sendDeleteNotification(userChatId)
    }

    @Transactional
    override fun getHistory(idUser: Long) = registrationBot.sendRecordToMng(historyRepository
        .findByClient(idUser.toInt())
        .map { it.toRecord().toString() })
}
