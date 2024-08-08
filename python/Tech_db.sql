DROP DATABASE IF EXISTS tech_db;
CREATE DATABASE tech_db
DEFAULT CHARACTER SET utf8
DEFAULT COLLATE utf8_general_ci;
USE tech_db;

CREATE TABLE workers
(
    id int NOT NULL AUTO_INCREMENT,
    w_login varchar(256) not null unique,
    w_passhash varchar(256) not null,
    w_name varchar(256) not null,
    CONSTRAINT PK_worker PRIMARY KEY(id)
);

CREATE TABLE wg_bonds
(
    id not null AUTO_INCREMENT,
    w_login varchar(256) not null,
    g_name varchar(256) not null
);

CREATE TABLE groups
(
    id int not null AUTO_INCREMENT,
    g_name varchar(256) not null unique,
    CONSTRAINT PK_groups PRIMARY KEY(id)
);


CREATE TABLE operations
(
    id int not null AUTO_INCREMENT,
    serial_number int not null,
    operation varchar(128) not null,
    worker varchar(256) not null,
    dt date not null,
    CONSTRAINT PK_operations PRIMARY KEY(id)
);