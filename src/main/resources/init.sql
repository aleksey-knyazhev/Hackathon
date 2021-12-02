alter type status owner to postgres;

create table clients
(
    id serial
        constraint clients_pk
            primary key,
    phone varchar(11) not null,
    telegram_id numeric(10,0) not null,
    user_name varchar(100) not null,
    first_name varchar(100),
    last_name varchar(100)
);
alter table clients
    owner to postgres;
create unique index telegram_id_uindex
    on clients (telegram_id);


create table schedule
(
    id          serial
        constraint schedule_pk
            primary key,
    time_start timestamp                         not null,
    time_end   timestamp                         not null,
    status      varchar(15)                      not null,
    client      integer
);
alter table schedule
    owner to postgres;
create unique index schedule_time_start_uindex
    on schedule (time_start);


create table history
(
    id          serial
        constraint history_pk
            primary key,
    client      integer                                         not null,
    date        timestamp                                       not null,
    action      status                                                  ,
    description varchar(200)
);
alter table history
    owner to postgres;


insert into clients (phone, telegram_id, user_name, first_name, last_name) values ('9011231212', 123456789, '@Pentrov', 'Петр', 'Петров');
insert into clients (phone, telegram_id, user_name, first_name, last_name) values ('9081231212', 234567890, '@Ivanov', 'Иван', 'Иванов');
insert into schedule (time_start, time_end, status, client) values ('2021-11-29 17:47:02.000000', '2021-11-29 17:47:04.000000', 'не подтверждено', 1);
insert into history (client, date, action, description) values (1, '2021-11-30 17:06:29.000000', 'не подтверждено', 'Создал неподтвержденную запись');