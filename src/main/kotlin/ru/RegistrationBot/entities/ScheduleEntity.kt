package ru.RegistrationBot.entities

import javax.persistence.*

@Entity
@Table(name = "schedule", schema = "public", catalog = "RegistrationBot")
open class ScheduleEntity {
    @get:Id
    @get:Column(name = "id", nullable = false)
    var id: Int = 0

    @get:Basic
    @get:Column(name = "timeStart", nullable = false)
    var timeStart: java.sql.Timestamp? = null

    @get:Basic
    @get:Column(name = "timeEnd", nullable = false)
    var timeEnd: java.sql.Timestamp? = null

    @get:Basic
    @get:Column(name = "status", nullable = false)
    var status: String = "свободно"

    @get:Basic
    @get:Column(name = "client", nullable = true)
    var client: Int? = null


    override fun toString(): String =
        "Entity of type: ${javaClass.name} ( " +
                "id = $id " +
                "timeStart = $timeStart " +
                "timeEnd = $timeEnd " +
                "status = $status " +
                "client = $client " +
                ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ScheduleEntity

        if (id != other.id) return false
        if (timeStart != other.timeStart) return false
        if (timeEnd != other.timeEnd) return false
        if (status != other.status) return false
        if (client != other.client) return false

        return true
    }

}

