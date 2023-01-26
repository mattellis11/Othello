--script to create the game database

drop table user;

create TABLE user(
username VARCHAR(24),
password varbinary(16));

alter table user add constraint user_username_pk PRIMARY KEY(username);
  


