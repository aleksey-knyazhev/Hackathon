package ru.registrationbot.api.converter

import ru.registrationbot.api.dto.ClientDto
import ru.registrationbot.api.dto.RecordDto
import ru.registrationbot.impl.entities.ClientsEntity
import ru.registrationbot.impl.entities.HistoryEntity

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