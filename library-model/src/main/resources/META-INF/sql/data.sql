
delete from user_role;
delete from users;
insert into users (created_at, name, email, password, type) values(current_timestamp,'Admin','admin@tutorial.com','rHz3r8Wg+M951cZv7ru0jdFqMegeCgG65oY7WH+MBv4=','EMPLOYEE');
insert into user_role (user_id, role) values((select id from users where email = 'admin@tutorial.com'), 'EMPLOYEE');
insert into user_role (user_id, role) values((select id from users where email = 'admin@tutorial.com'), 'ADMINISTRATOR');