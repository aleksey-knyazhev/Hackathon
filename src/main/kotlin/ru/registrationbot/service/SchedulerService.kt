package ru.registrationbot.service

import java.time.LocalDate

interface SchedulerService {

    /**
     * Метод для открытия записи
     * date - день для записи
     * time - временной диапазон (строка вида "10:00 18:00")
     */
    fun openRecording(date: LocalDate, time: String)

    /**
     * Метод для получения дат, на которые открыта запись
     */
    fun getDates():List<LocalDate>

    /**
     * Метод для получения свободных слотов времени для конкретной даты
     * Список должен состоять из строк, содержащих id записи и время, разделенных пробелом (257 10:00)
     */
    fun getTimesForDate(date: LocalDate):List<String>

}