DROP TABLE IF EXISTS Customer_458882;

CREATE TABLE Customer_458882
(
    id int(36) NOT NULL,
    email varchar(200) NOT NULL,
    first_name varchar(500) DEFAULT NULL,
    last_name varchar(500) DEFAULT NULL,
    PRIMARY KEY (id)
);
