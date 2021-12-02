package ru.RegistrationBot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class RegistrationBot : TelegramLongPollingBot() {
    @Value("\${telegram.botName}")
    private val botName: String = ""

    @Value("\${telegram.token}")
    private val token: String = ""

    @Value("\${telegram.doctor.chatId}")
    private val doctor: Long = 0

    override fun getBotToken(): String = token

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            val message = update.message
            val chatId = message.chatId
            var buttons: MutableList<String> = mutableListOf("Главное меню")
            var date: String
            var time: String
            val responseText = if (message.hasText()) {
                val messageText = message.text
                val text = if (chatId == doctor) {
                    when {
                        messageText == "/start" -> {
                            buttons.add("Открыть запись")
                            buttons.add("Показать свободное время")
                            "Здравствуй, хозяин!"

                        }
                        messageText.startsWith("Открыть запись") -> "Введите дату в формате ГГГГ-ММ-ДД\nНапример: 2021-10-08"
                        messageText.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> {
                            date = messageText
                            "Введите время первой и последней записи через пробел в формате hh:mm\nНапример: 10:00 18:30"
                        }
                        messageText.matches(Regex("\\d{2}:\\d{2} \\d{2}:\\d{2}")) -> {
                            time = messageText
                            //метод, который принимает дату и время для открытия записи - создается таблица в бд
                            // LocalDate.parse(date)
                            "Запись открыта успешно"
                        }
                        messageText.startsWith("Главное меню") -> {
                            date = ""
                            time = ""
                            "/start"
                        }
                        else -> "Вы написали: *$messageText*"
                    }
                } else {
                    when {
                        messageText == "/start" -> {
                            buttons.add("Показать свободное время")
                            "Добро пожаловать!"
                        }
                        else -> "Вы написали: *$messageText*"
                    }
                }
                when {
                    messageText.startsWith("Показать свободное время") -> {
                        //метод, возвращающий дни с открытой записью
                        buttons.addAll(listOf("10-05-2021", "08-06-2021", "22-12-2021"))
                        "Выберите дату"
                    }
                    messageText.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) -> {
                        date = messageText
                        buttons.addAll(listOf("10:00", "15:00", "17:30"))
                        "Выберите свободное время для записи"
                    }
                    messageText.matches(Regex("\\d{2}:\\d{2}")) -> {
                        date = messageText
                        time = messageText
                        requestConfirmation(chatId, date, time)
                        when {
                            messageText.startsWith("Подтвердить запись") -> "Запись создана успешно"


                            else -> "Запись не подтверждена"
                        }

                            //метод, который принимает дату и время для создания записи
                            // LocalDate.parse(date)
                        }

                    else -> text
                }
            } else {
                "Я понимаю только текст"
            }

            sendNotification(chatId, responseText, buttons)
        }
    }

    private fun sendNotification(chatId: Long, responseText: String, buttons: List<String>) {
        val responseMessage = SendMessage(chatId.toString(), responseText)
        responseMessage.enableMarkdown(true)

        responseMessage.replyMarkup = getReplyMarkup(buttons)


        execute(responseMessage)
    }

    private fun getReplyMarkup(allButtons: List<String>): ReplyKeyboardMarkup {
        val markup = ReplyKeyboardMarkup()
        markup.keyboard = allButtons.map { rowButton ->
            val row = KeyboardRow()
            row.add(rowButton)
            row
        }
        return markup
    }

    private fun requestConfirmation(chatId: Long, date: String, time: String) {
        var buttons: List<String> = listOf(
            "Главное меню",
            "Подтвердить запись",
            "Отменить запись"
        )
        val text = "Хотели бы вам напомнить, что $date в $time вы записаны на прием. Подтвердите запись или отмените ее"
        sendNotification(chatId, text, buttons)
    }

}