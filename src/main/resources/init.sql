create table clients
(
    id serial
        constraint clients_pk
            primary key,
    phone varchar(11) not null,
    chat_id numeric(10,0) not null,
    user_name varchar(100) not null,
    first_name varchar(100),
    last_name varchar(100)
);
create unique index chat_id_uindex
    on clients (chat_id);


create table schedule
(
    id          SERIAL PRIMARY KEY,
    record_date DATE    NOT NULL,
    time_start  TIME    NOT NULL,
    time_end    TIME    NOT NULL,
    status      VARCHAR NOT NULL,
    client      INTEGER
);

create table history
(
    id serial
        constraint history_pk
            primary key,
    client      integer                                         not null,
    date        timestamp                                       not null,
    action      varchar(100)                                                  ,
    description varchar(200)
);

insert into clients (phone, chat_id, user_name, first_name, last_name) values ('9011231212', 123456789, '@Pentrov', 'Петр', 'Петров');
insert into clients (phone, chat_id, user_name, first_name, last_name) values ('9081231212', 234567890, '@Ivanov', 'Иван', 'Иванов');
insert into schedule (record_date, time_start, time_end, status, client) values ('2021-11-29', '17:47:02', '17:47:00', 'FREE', 1);
insert into history (client, date, action, description) values (1, '2021-11-30 17:06:29.000000', 'не подтверждено', 'Создал неподтвержденную запись');