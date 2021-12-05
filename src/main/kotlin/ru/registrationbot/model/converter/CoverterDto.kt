package ru.registrationbot.model.converter

import ru.registrationbot.model.dto.ClientDto
import ru.registrationbot.model.dto.RecordDto
import ru.registrationbot.model.entities.ClientsEntity
import ru.registrationbot.model.entities.HistoryEntity

fun ClientsEntity.toClient() = ClientDto(
    idRecording = checkNotNull(id).toLong(),
    phone = phone,
    chatId = chatId,
    userName = userName,
    firstName = firstName,
    lastName = lastName
)

fun HistoryEntity.toRecord() = RecordDto(
    id = id,
    client = client,
    date = date,
    action = action,
    description = description
)