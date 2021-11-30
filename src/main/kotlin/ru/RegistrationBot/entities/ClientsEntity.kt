package ru.RegistrationBot.entities

import javax.persistence.*

@Entity
@Table(name = "clients", schema = "public", catalog = "RegistrationBot")
open class ClientsEntity {
    @get:Id
    @get:Column(name = "id", nullable = false)
    var id: Int? = null

    @get:Basic
    @get:Column(name = "phone", nullable = false)
    var phone: Long? = null

    @get:Basic
    @get:Column(name = "phoneString", nullable = true)
    var phoneString: String? = null

    @get:Basic
    @get:Column(name = "name", nullable = true)
    var name: String? = null


    override fun toString(): String =
        "Entity of type: ${javaClass.name} ( " +
                "id = $id " +
                "phone = $phone " +
                "phoneString = $phoneString " +
                "name = $name " +
                ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ClientsEntity

        if (id != other.id) return false
        if (phone != other.phone) return false
        if (phoneString != other.phoneString) return false
        if (name != other.name) return false

        return true
    }

}

