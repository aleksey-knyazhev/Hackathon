package ru.registrationbot.entities

import javax.persistence.*

@Entity
@Table(name = "clients", schema = "public", catalog = "RegistrationBot")
data class ClientsEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null,

    @Column(name = "phone")
    var phone: String?,

    @Column(name = "chat_id", nullable = false)
    var chatId: Long,

    @Column(name = "user_name")
    var userName: String? = null,

    @Column(name = "firstName")
    var firstName: String? = null,

    @Column(name = "lastName")
    var lastName: String? = null
)

