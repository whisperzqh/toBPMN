create table BPMN_event
(
    id          integer     not null
        primary key autoincrement,
    e_id        varchar(50) not null,
    name        varchar(50) not null,
    description text        not null,
    process_id  text        not null
);

create table BPMN_implement
(
    id                integer      not null
        primary key autoincrement,
    execute_condition varchar(100) not null,
    execute_action    varchar(100) not null
);

create table BPMN_parameter
(
    id   integer     not null
        primary key autoincrement,
    p_id varchar(50) not null,
    name varchar(50) not null,
    typr varchar(50) not null
);

create table BPMN_process
(
    id        integer     not null
        primary key autoincrement,
    t_id      varchar(50) not null,
    executor  varchar(50),
    action    varchar(50) not null,
    recipient varchar(50),
    type      varchar(50)
);

create table BPMN_process_execute
(
    id           integer not null
        primary key autoincrement,
    process_id   integer not null
        references BPMN_process
            deferrable initially deferred,
    implement_id integer not null
        references BPMN_implement
            deferrable initially deferred
);

create index BPMN_process_execute_implement_id_c09ffd06
    on BPMN_process_execute (implement_id);

create index BPMN_process_execute_process_id_5f999634
    on BPMN_process_execute (process_id);

create unique index BPMN_process_execute_process_id_implement_id_eca87bfe_uniq
    on BPMN_process_execute (process_id, implement_id);

create table BPMN_subprocess
(
    id          integer     not null
        primary key autoincrement,
    sp_id       varchar(50) not null,
    name        varchar(50) not null,
    description text        not null,
    process_id  text        not null
);