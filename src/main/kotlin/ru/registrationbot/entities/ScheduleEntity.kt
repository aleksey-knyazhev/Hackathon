package ru.registrationbot.entities

import ru.registrationbot.enum.TimeslotStatus
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "schedule", schema = "public", catalog = "RegistrationBot")
class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    lateinit var recordDate: LocalDate

    lateinit var timeStart: LocalTime

    lateinit var timeEnd: LocalTime

    @Enumerated(EnumType.STRING)
    lateinit var status: TimeslotStatus

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

enum class State{
    FREE,
    BUSY,
    CONFIRM
}


