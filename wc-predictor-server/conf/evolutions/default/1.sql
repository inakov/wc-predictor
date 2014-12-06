# --- First database schema
# --- !Ups

create sequence s_room_id;
create sequence s_room_status_id;

create table room (
  id    int DEFAULT nextval('s_room_id'),
  floor int NOT NULL,
  name  varchar(128)
);

create table room_status (
  id    int DEFAULT nextval('s_room_status_id'),
  roomId int,
  statusType int,
  creationDate TIMESTAMP,
  statusExpiration TIMESTAMP
);

INSERT INTO room VALUES (nextval('s_room_id'), 5, 'Left');
INSERT INTO room VALUES (nextval('s_room_id'), 5, 'Right');
INSERT INTO room VALUES (nextval('s_room_id'), 6, 'Left');
INSERT INTO room VALUES (nextval('s_room_id'), 6, 'Right');

# --- !Downs

drop table room;
drop sequence s_room_id;
drop table room_status;
drop sequence s_room_status_id;