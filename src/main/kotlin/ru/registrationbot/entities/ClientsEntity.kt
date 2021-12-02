package ru.registrationbot.entities

import javax.persistence.*

@Entity
@Table(name = "clients", schema = "public", catalog = "RegistrationBot")
class ClientsEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null,

    @Column(name = "phone", nullable = false)
    var phone: String,

    @Column(name = "telegram_id", nullable = false)
    var chatId: Long,

    @Column(name = "user_name")
    var userName: String? = null,

    @Column(name = "firstName")
    var firstName: String? = null
)

