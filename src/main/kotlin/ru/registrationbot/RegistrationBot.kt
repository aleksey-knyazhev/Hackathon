package ru.registrationbot

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.registrationbot.impl.entities.HistoryEntity
import ru.registrationbot.impl.entities.ScheduleEntity
import java.time.Duration
import java.time.LocalDateTime


@EnableScheduling
@Service
class RegistrationBot : TelegramLongPollingBot() {
    @Value("\${telegram.botName}")
    private val botName: String = ""

    @Value("\${telegram.token}")
    private val token: String = ""

    @Value("\${telegram.doctor.chatId}")
    private val doctor: Long = 0

    //видимость chatid на весь класс
    var chatId = 1L
    override fun getBotToken(): String = token

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            val message = update.message
            chatId = message.chatId
            val buttons: MutableList<String> = mutableListOf("Главное меню")
            val date: String
            val time: String
            val responseText = if (message.hasText()) {
                val messageText = message.text
                val text = if (chatId == doctor) {
                    when {
                        messageText == "/start" -> {
                            buttons.add("Открыть запись")
                            buttons.add("Показать свободное время")
                            buttons.add("Показать список подтвержденных записей на завтра")
                            buttons.add("Показать список неподтвержденных записей на завтра")
                            buttons.add("Показать список клиентов")
                            "Здравствуй, хозяин!"
                        }
                        messageText.startsWith("Открыть запись") -> "Введите дату в формате ГГГГ-ММ-ДД\nНапример: 2021-10-08"
                        messageText.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> {
                            date = messageText
                            "Введите время первой и последней записи через пробел в формате hh:mm\nНапример: 10:00 18:30"
                        }
                        messageText.matches(Regex("\\d{2}:\\d{2} \\d{2}:\\d{2}")) -> {
                            time = messageText
                            //telegramBotService.openRecording(LocalDate.parse(date), time)
                            "Запись открыта успешно"
                        }
                        messageText.startsWith("Показать список подтвержденных записей на завтра") -> {
                            //telegramBotService.getConfirmedRecording()
                            "Для удаления записи введите команду \"Отменить id\", где id - номер записи"
                        }
                        messageText.startsWith("Показать список неподтвержденных записей на завтра") -> {
                            //telegramBotService.getUnconfirmedRecording()
                            "Для удаления записи введите команду \"Отменить id\", где id - номер записи"
                        }
                        messageText.startsWith("Отменить") -> {
                            //telegramBotService.deleteRecording(messageText.replace(messageText.split("")[1])
                            "Запись удалена"
                        }
                        messageText.startsWith("Показать список клиентов") -> {
                            //telegramBotService.getAllUsers()
                            "Для получения истории по клиенту введите команду \"История id\"\n" +
                                    "Для удаления информации о пользователе и его записей введите команду \"Удалить id\"\n" +
                                    "id - номер пользователя"
                        }
                        messageText.startsWith("История") -> {
                            //telegramBotService.getHistory(messageText.split("")[1])
                            "Вернуться в главное меню /start"
                        }
                        messageText.startsWith("Удалить") -> {
                            //telegramBotService.deleteUserInfo(messageText.split("")[1])
                            "Пользователь и его записи удалены"
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
                        messageText.startsWith("Подтвердить") -> {
                            //telegramBotService.confirmRecording(UserInfo(message))
                            "Запись успешно подтверждена"
                        }
                        messageText.startsWith("Отменить запись") -> {
                            //telegramBotService.cancelRecording(UserInfo(message))
                            "Запись отменена"
                        }
                        else -> "Вы написали: *$messageText*"
                    }
                }
                when {
                    messageText.startsWith("Показать свободное время") -> {
                        //buttons.addAll(telegramBotService.getDates())
                        buttons.addAll(listOf("10-05-2021", "08-06-2021", "22-12-2021"))
                        "Выберите дату"
                    }
                    messageText.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) -> {
                        //buttons.addAll(telegramBotService.getTimesForDate(LocalDate.parse(messageText)))
                        buttons.addAll(listOf("1 10:00", "34 15:00", "245 17:30"))
                        "Выберите свободное время для записи"
                    }
                    messageText.matches(Regex("\\d{2}:\\d{2}")) -> {
                        //telegramBotService.addRecording(messageText.split("")[0], UserInfo(message))
                        "Запись создана успешно"
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
        val buttons: List<String> = listOf(
            "Главное меню",
            "Подтвердить запись",
            "Отменить запись"
        )
        val text = "Хотели бы вам напомнить, что $date в $time вы записаны на прием. Подтвердите запись или отмените ее"
        sendNotification(chatId, text, buttons)
    }

    private fun sendCancelNotification(chatId: Long, time: String) {
        val buttons: List<String> = listOf("Главное меню")
        val text = "Извините, Ваша запись на завтра в $time отменена"
        sendNotification(chatId, text, buttons)
    }

    private fun sendDeleteNotification(chatId: Long) {
        val buttons: List<String> = listOf("Главное меню")
        val text = "Все Ваши записи были удалены"
        sendNotification(chatId, text, buttons)
    }

    @Scheduled(cron = "0 0 0 * * *")
    private fun sendNotificationByShedule(){
        val historyData = mutableListOf<HistoryEntity>()
        val currentDate =  LocalDateTime.now()
        for(history in historyData){
            val date = history.date
            val duration = Duration.between(currentDate, date)
            if(duration.toDays() == 1L){
                val schedule = mutableListOf<ScheduleEntity>()
                for(sc in schedule)
                //Пока непонятно как передавать chatId и time
                    requestConfirmation(chatId,history.date.toString(),sc.timeStart.toString())
            }
        }
    }
}