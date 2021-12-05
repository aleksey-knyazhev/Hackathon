create table clients
(
    id serial
        constraint clients_pk
            primary key,
    phone varchar(11),
    chat_id numeric(10,0) not null,
    user_name varchar(100) not null,
    first_name varchar(100),
    last_name varchar(100)
);
create unique index chat_id_uindex
    on clients (chat_id);


create table schedule
(
    id          serial primary key,
    record_date date    not null,
    time_start  time    not null,
    time_end    time    not null,
    status      varchar not null,
    client      integer
);

create table history
(
    id serial
        constraint history_pk
            primary key,
    client      integer not null,
    date        timestamp not null,
    action      varchar(100),
    description varchar(200)
);