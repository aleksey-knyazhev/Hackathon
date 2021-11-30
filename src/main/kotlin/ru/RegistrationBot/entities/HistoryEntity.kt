package ru.RegistrationBot.entities

import javax.persistence.*

@Entity
@Table(name = "history", schema = "public", catalog = "RegistrationBot")
open class HistoryEntity {
    @get:Id
    @get:Column(name = "id", nullable = false)
    var id: Int? = null

    @get:Basic
    @get:Column(name = "clinet", nullable = false)
    var clinet: Int? = null

    @get:Basic
    @get:Column(name = "date", nullable = false)
    var date: java.sql.Timestamp? = null

    @get:Basic
    @get:Column(name = "action", nullable = false)
    var action: String = "прочее"

    @get:Basic
    @get:Column(name = "description", nullable = true)
    var description: String? = null


    override fun toString(): String =
        "Entity of type: ${javaClass.name} ( " +
                "id = $id " +
                "clinet = $clinet " +
                "date = $date " +
                "action = $action " +
                "description = $description " +
                ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HistoryEntity

        if (id != other.id) return false
        if (clinet != other.clinet) return false
        if (date != other.date) return false
        if (action != other.action) return false
        if (description != other.description) return false

        return true
    }

}

