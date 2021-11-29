/*drop database if exists "KotlinRegistrationBot";

create database "KotlinRegistrationBot"
    with owner postgres;*/

create type status as enum ('свободно', 'не подтверждено', 'подтверждено');


create table clients
(
    id            serial
        constraint clients_pk
            primary key,
    phone         bigint not null,
    "phoneString" varchar(18),
    name          varchar(100)
);

alter table clients
    owner to postgres;

create unique index clients_id_uindex
    on clients (id);

create unique index clients_phone_uindex
    on clients (phone);



create table schedule
(
    id          serial
        constraint schedule_pk
            primary key,
    "timeStart" timestamp                         not null,
    "timeEnd"   timestamp                         not null,
    status      status default 'свободно'::status not null,
    client      integer
);

alter table schedule
    owner to postgres;

create unique index schedule_id_uindex
    on schedule (id);

create unique index schedule_timestart_uindex
    on schedule ("timeStart");

create unique index schedule_timeend_uindex
    on schedule ("timeEnd");

