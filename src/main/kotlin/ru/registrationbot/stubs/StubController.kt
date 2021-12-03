package ru.registrationbot.stubs

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
import ru.registrationbot.dto.UserInfo
import kotlin.random.Random


@RestController
class StubController(@Autowired private var service: ClientService) {

    final val json = """{"chat": {"id" : "${Random.nextLong(1000L)}",
        |                         "first_name" : "Петр",
        |                         "last_name" : "Петров",
        |                         "username" : "any"},
        |                        "contact" : {"phone_number": "1234567891"}}""".trimMargin()
    final val mapper = jacksonObjectMapper()


    var message: Message = mapper.readValue<Message>(json)

    val userInfo = UserInfo(message)


    @GetMapping(value = ["/add/{id}"])
    fun addRecord(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(service.addRecording(id, userInfo))

    }

}