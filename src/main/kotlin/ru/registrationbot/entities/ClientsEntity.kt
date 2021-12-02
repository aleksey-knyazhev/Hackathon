package ru.registrationbot.entities

import javax.persistence.*

@Entity
@Table(name = "clients", schema = "public", catalog = "RegistrationBot")
open class ClientsEntity {
    @Id
    @Column(name = "id", nullable = false)
    var id: Int = 0

    @Column(name = "chatId", nullable = false)
    var chatId: String? = null

    @Column(name = "name", nullable = true)
    var name: String? = null


    override fun toString(): String =
        "Entity of type: ${javaClass.name} ( " +
                "id = $id " +
                "telegramId = $chatId " +
                "name = $name " +
                ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ClientsEntity

        if (id != other.id) return false
        if (chatId != other.chatId) return false
        if (name != other.name) return false

        return true
    }

}

