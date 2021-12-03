package ru.registrationbot.impl.stubs

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.objects.Message
import ru.registrationbot.api.service.ClientService
import ru.registrationbot.api.dto.UserInfo
import kotlin.random.Random


@RestController
class StubController(@Autowired private var service: ClientService) {

    //idTimeSlot - id записи таблицы Scheduler с проверкой добавления клиента
    @GetMapping(value = ["/add/{idTimeSlot}"])
    fun addRecording(@PathVariable idTimeSlot: Long): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(service.addRecording(idTimeSlot, UserInfo(getMessage(Random.nextLong(1000L)))))

    }

    //idTimeSlot - id записи таблицы Scheduler
    // idChatClient- идентификатор чата из таблицы клиентов(это только для тестов - так он будет приходить к нам естественным путес с бота)
    @GetMapping(value = ["/add/{idTimeSlot}/{idChatClient}"])
    fun addRecording(@PathVariable idTimeSlot: Long, @PathVariable idChatClient: Long): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(service.addRecording(idTimeSlot, UserInfo(getMessage(idChatClient))))

    }

    //idTimeSlot - id записи таблицы Scheduler
    @GetMapping(value = ["/delete/{idTimeSlot}"])
    fun delRecording(@PathVariable idTimeSlot: Long): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(service.deleteRecording(idTimeSlot))

    }

    //idUser - id записи таблицы Client
    @GetMapping(value = ["/cancel/{idUser}"])
    fun cancelRecording(@PathVariable idUser: Long): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(service.cancelRecording(UserInfo(getMessage(idUser))))

    }

    //idUser - id записи таблицы Client
    @GetMapping(value = ["/confirm/{idUser}"])
    fun confirmRecord(@PathVariable idUser: Long): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(service.confirmRecording(UserInfo(getMessage(idUser))))

    }


    private fun getMessage(chatId: Long): Message {

        val json = """{"chat": {"id" : "$chatId",
        |                         "first_name" : "Петр",
        |                         "last_name" : "Петров",
        |                         "username" : "any"},
        |                         "contact" : {"phone_number": "1234567891"}}""".trimMargin()

        val mapper = jacksonObjectMapper()

        return mapper.readValue(json)
    }
}