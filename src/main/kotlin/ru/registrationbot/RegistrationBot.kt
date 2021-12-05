package ru.registrationbot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.registrationbot.api.dto.UserInfo
import ru.registrationbot.api.service.ClientService
import ru.registrationbot.api.service.ManagerService
import ru.registrationbot.api.service.ReportService
import ru.registrationbot.api.service.SchedulerService
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@EnableScheduling
@Service
class RegistrationBot : TelegramLongPollingBot() {
    @Value("\${telegram.botName}")
    private val botName: String = ""

    @Value("\${telegram.token}")
    private val token: String = ""

    @Value("\${telegram.doctor.chatId}")
    private val manager: Long = 0

    //видимость chatid на весь класс
    var chatId = 1L
    var date = ""
    var time = ""

    @Autowired
    lateinit var scheduleService: SchedulerService

    @Autowired
    @Lazy
    lateinit var clientService: ClientService

    @Autowired
    lateinit var reportService: ReportService

    @Autowired
    @Lazy
    lateinit var managerService: ManagerService

    override fun getBotToken(): String = token

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            val message = update.message
            chatId = message.chatId
            val buttons: MutableList<String> = mutableListOf("Главное меню")
            val responseText = if (message.hasText()) {
                val messageText = message.text
                val text = if (chatId == manager) {
                    when {
                        messageText == "/start" -> {
                            buttons.add("Открыть запись")
                            buttons.add("Показать свободное время")
                            buttons.add("Показать список подтвержденных записей на завтра")
                            buttons.add("Показать список неподтвержденных записей на завтра")
                            buttons.add("Показать список клиентов")
                            "Здравствуй, хозяин!"
                        }
                        messageText.startsWith("Открыть запись") -> "Введите дату в формате ГГГГ-ММ-ДД\nНапример: 2022-10-08"
                        messageText.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> {
                            date = messageText
                            "Введите время первой и последней записи через пробел в формате hh:mm\nНапример: 10:00 18:00"
                        }
                        messageText.matches(Regex("\\d{2}:\\d{2} \\d{2}:\\d{2}")) -> {
                            time = messageText
                            scheduleService.openRecording(LocalDate.parse(date), time)
                            "Запись открыта успешно"
                        }
                        messageText.startsWith("Показать список подтвержденных записей на завтра") -> {
                            reportService.getConfirmedRecording()
                            "Для удаления записи введите команду \"Отменить id\", где id - номер записи"
                        }
                        messageText.startsWith("Показать список неподтвержденных записей на завтра") -> {
                            reportService.getUnconfirmedRecording()
                            "Для удаления записи введите команду \"Отменить id\", где id - номер записи"
                        }
                        messageText.startsWith("Отменить") -> {
                            clientService.deleteRecording(messageText.split(" ")[1].toLong())
                            "Запись удалена"
                        }
                        messageText.startsWith("Показать список клиентов") -> {
                            managerService.getAllUsers()
                            "Для получения истории по клиенту введите команду \"История id\"\n" +
                                    "Для удаления информации о пользователе и его записей введите команду \"Удалить id\"\n" +
                                    "id - номер пользователя"
                        }
                        messageText.startsWith("История") -> {
                            managerService.getHistory(messageText.split(" ")[1].toLong())
                            "Для удаления информации о пользователе и его записей введите команду \"Удалить id\"\n" +
                                    "id - номер пользователя"
                        }
                        messageText.startsWith("Удалить") -> {
                            managerService.deleteUserInfo(messageText.split(" ")[1].toLong())
                            "Пользователь и его записи удалены"
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
                            clientService.confirmRecording(UserInfo(message))
                            "Запись успешно подтверждена"
                        }
                        messageText.startsWith("Отменить запись") -> {
                            clientService.cancelRecording(UserInfo(message))
                            "Запись отменена"
                        }
                        else -> "Вы написали: *$messageText*"
                    }
                }
                when {
                    messageText.startsWith("Показать свободное время") -> {
                        scheduleService.getDates()
                            .forEach { buttons.add(it.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()) }
                        "Выберите дату"
                    }
                    messageText.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) -> {
                        buttons.addAll(scheduleService.getTimesForDate(LocalDate.parse(messageText,
                            DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                        "Выберите свободное время для записи"
                    }
                    messageText.matches(Regex("\\d{2}-\\d{2}-\\d{4}  \\d{2}:\\d{2}-\\d{2}:\\d{2}")) -> {
                        val dateTime = messageText.split("  ")
                        clientService.addRecording(LocalDate.parse(dateTime[0], DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                            LocalTime.parse(dateTime[1].split("-")[0]), UserInfo(message))
                        "Запись создана успешно"
                    }
                    messageText.startsWith("Главное меню") -> {
                        date = ""
                        time = ""
                        "/start"
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

    /**
     * Для отправки клиенту сообщения для подтверждения записи
     */
    private fun requestConfirmation(chatId: Long, date: String, time: String) {
        val buttons: List<String> = listOf(
            "Главное меню",
            "Подтвердить запись",
            "Отменить запись"
        )
        val text = "Хотели бы вам напомнить, что $date в $time вы записаны на прием. Подтвердите запись или отмените ее"
        sendNotification(chatId, text, buttons)
    }

    /**
     * Для отправки уведомления клиенту об отмененной записи
     */
    fun sendCancelNotificationToClient(chatId: Long, time: String) {
        val buttons: List<String> = listOf("Главное меню")
        val text = "Извините, Ваша запись на завтра в $time отменена"
        sendNotification(chatId, text, buttons)
    }

    /**
     * Для отправки уведомления клиенту об удалении его данных из БД
     */
    fun sendDeleteNotification(chatId: Long) {
        val buttons: List<String> = listOf("Главное меню")
        val text = "Все Ваши записи были удалены"
        sendNotification(chatId, text, buttons)
    }

    /**
     * Для отправки уведомления менеджеру о отмене записи клиентом
     */
    fun sendCancelNotificationToMng(userName: String, time: String) {
        val buttons: List<String> = listOf("Главное меню")
        val text = "Клиент $userName отменил запись на завтра в $time"
        sendNotification(manager, text, buttons)
    }

    /**
     * Для отправки списка записей/истории/списка клиентов менеджеру
     */
    fun sendRecord(records: List<String>) {
        val buttons: List<String> = listOf("Главное меню")
        sendNotification(manager, records.joinToString("\n"), buttons)
    }

    @Scheduled(cron = "7 0 0 * * *")
    private fun sendNotificationBySchedule() {
        val currentDate = LocalDateTime.now()
        for (date in scheduleService.getDates()) {
            val duration = Duration.between(currentDate, date)
            if (duration.toDays() == 1L) {
                for (client in clientService.getBookedTimeWithClient(date)) {
                    requestConfirmation(client.chatId, date.toString(), client.timeStart.toString())
                }
            }
        }
    }
}