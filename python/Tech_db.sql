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
    id int not null AUTO_INCREMENT,
    w_login int not null,
    g_name int not null,
    constraint PK_bonds primary key(id)
);

CREATE TABLE user_groups
(
    id int not null AUTO_INCREMENT,
    g_name varchar(256) not null unique,
    access int not null,
    CONSTRAINT PK_groups PRIMARY KEY(id)
);


CREATE TABLE operations
(
    id int not null AUTO_INCREMENT,
    serial_number int not null,
    operation varchar(128) not null,
    worker varchar(256) not null,
    dt datetime not null,
    CONSTRAINT PK_operations PRIMARY KEY(id)
);


CREATE TABLE decimals
(
    id int not null AUTO_INCREMENT,
    num varchar(32) unique,
    d_name varchar(256),
    d_type varchar(128),
    CONSTRAINT PK_decimals PRIMARY KEY(id)
);

CREATE TABLE devices
(
    serial_number int not null unique,
    decimal_id int,
    CONSTRAINT PK_devices PRIMARY KEY(serial_number),
    CONSTRAINT FK_devices FOREIGN KEY(decimal_id) REFERENCES decimals(id)
);


CREATE TABLE sessions
(
    id int not null AUTO_INCREMENT,
    iv varchar(256),
    aes_key varchar(256),
    date_last_conn datetime,
    CONSTRAINT PK_sessions PRIMARY KEY (id)
);




