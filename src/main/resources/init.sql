create type status as enum ('свободно', 'не подтверждено', 'подтверждено', 'прочее');
alter type status owner to postgres;

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


create table history
(
    id          serial
        constraint history_pk
            primary key,
    clinet      integer                                         not null,
    date        timestamp                                       not null,
    action      status default 'прочее'::status not null,
    description varchar(200)
);
alter table history
    owner to postgres;
create unique index history_id_uindex
    on history (id);


insert into clients (phone, "phoneString", name) values (79857197650, '+7 (901)123-12-12', 'Петров');
insert into clients (phone, "phoneString", name) values (2, '02', 'Иванов');
insert into schedule ("timeStart", "timeEnd", status, client) values ('2021-11-29 17:47:02.000000', '2021-11-29 17:47:04.000000', 'не подтверждено', 1);
insert into history (clinet, date, action, description) values (1, '2021-11-30 17:06:29.000000', 'не подтверждено', 'Создал неподтвержденную запись');