CREATE TABLE IF NOT EXISTS machines (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  caffeine int(11) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
  id int(11) NOT NULL AUTO_INCREMENT,
  login varchar(100) NOT NULL,
  password varchar(200) NOT NULL,
  email varchar(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (login),
  UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS coffee (
  id int(11) NOT NULL AUTO_INCREMENT,
  userId int(11) NOT NULL,
  machineId int(11) NOT NULL,
  timestamp datetime NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users (id),
  FOREIGN KEY (machineId) REFERENCES machines (id)
);

CREATE INDEX IF NOT EXISTS users_userId ON coffee (userId);
CREATE INDEX IF NOT EXISTS users_machineId ON coffee (machineId);
