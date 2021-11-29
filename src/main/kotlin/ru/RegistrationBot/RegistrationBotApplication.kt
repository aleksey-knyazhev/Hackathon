package ru.RegistrationBot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RegistrationBotApplication

fun main(args: Array<String>) {
	runApplication<RegistrationBotApplication>(*args)
}
