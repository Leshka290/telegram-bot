--liquibase formatted sql

create table users_data_table
(
    chat_id       bigint primary key not null,
    first_name    varchar(255),
    last_name     varchar(255),
    registered_at timestamp,
    user_name     varchar(255)
);

create table notification_task_table
(
    id        bigint primary key not null,
    date_time timestamp,
    task      varchar(255),
    user_id   bigint
);

insert into notification_task_table
values (1, '2023-07-19 11:27:00', ' 19.07.2023 11:27:00 Сделать домашнюю работу', 1244631469);