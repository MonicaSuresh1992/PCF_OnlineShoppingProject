DROP TABLE IF EXISTS Items_458882;

CREATE TABLE Items_458882
(
    id int(36) NOT NULL,
    name varchar(200) NOT NULL,
    description varchar(1000) DEFAULT NULL,
    price double(500) DEFAULT NULL,
    PRIMARY KEY (id)
);
