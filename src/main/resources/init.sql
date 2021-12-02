create type status as enum ('свободно', 'не подтверждено', 'подтверждено', 'прочее');
alter type status owner to postgres;

create table clients
(
    id serial
        constraint clients_pk
            primary key,
    phone bigint not null,
    telegram_id bigint not null,
    user_name varchar(100) not null,
    first_name varchar(100),
    last_name varchar(100)
);
alter table clients
    owner to postgres;
create unique index clients_id_uindex
    on clients (id);
create unique index clients_phone_uindex
    on clients (phone);


create table schedule
(
    id          SERIAL PRIMARY KEY,
    record_date DATE    NOT NULL,
    time_start  TIME    NOT NULL,
    time_end    TIME    NOT NULL,
    status      VARCHAR NOT NULL,
    client      INTEGER
);


-- alter table schedule
--     owner to postgres;
-- create
-- unique index schedule_id_uindex
--     on schedule (id);
-- create
-- unique index schedule_time_start_uindex
--     on schedule (time_start);


create table history
(
    id serial
        constraint history_pk
            primary key,
    clinet      integer not null,
    date        timestamp not null,
    action      status,
    description varchar(200)
);
alter table history
    owner to postgres;
create unique index history_id_uindex
    on history (id);


insert into clients (phone, telegram_id, user_name, first_name, last_name) values (9011231212, 123456789, '@Pentrov', 'Петр', 'Петров');
insert into clients (phone, telegram_id, user_name, first_name, last_name) values (9023456789, 234567890, '@Ivanov', 'Иван', 'Иванов');
insert into history (clinet, date, action, description) values (1, '2021-11-30 17:06:29.000000', 'не подтверждено'::status, 'Создал неподтвержденную запись');
insert into schedule (record_date, time_start, time_end, status, client) values ('2021-1-1', '10:00', '11:00', 'BOOKED', 1);