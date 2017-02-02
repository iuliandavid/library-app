delete from user_role;
delete from users;
insert into users (created_at, name, email, password, type) values(current_timestamp,'Admin','admin@tutorial.com','ZG5fMz1fYyV5V3RCYjh1UA==','EMPLOYEE');
insert into user_role (user_id, role) values((select id from users where email = 'admin@tutorial.com'), 'EMPLOYEE');
insert into user_role (user_id, role) values((select id from users where email = 'admin@tutorial.com'), 'ADMINISTRATOR');