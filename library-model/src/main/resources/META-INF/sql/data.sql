
delete from user_role;
delete from users;
insert into users (created_at, name, email, password, type) values(current_timestamp,'Admin','admin@tutorial.com','rHz3r8Wg+M951cZv7ru0jdFqMegeCgG65oY7WH+MBv4=','EMPLOYEE');
insert into user_role (user_id, role) values((select id from users where email = 'admin@tutorial.com'), 'EMPLOYEE');
insert into user_role (user_id, role) values((select id from users where email = 'admin@tutorial.com'), 'ADMINISTRATOR');

insert into categories (name) values ('Java');
insert into categories (name) values ('Architecture');
insert into categories (name) values ('Clean Code');
insert into categories (name) values ('Networks');

insert into authors (name) values ('Robert Martin');
insert into authors (name) values ('James Gosling');
insert into authors (name) values ('Martin Fowler');
insert into authors (name) values ('Erich Gamma');
insert into authors (name) values ('Richard Helm');
insert into authors (name) values ('Ralph Johnson');
insert into authors (name) values ('John Vlissides');
insert into authors (name) values ('Kent Beck');
insert into authors (name) values ('John Brandt');
insert into authors (name) values ('William Opdyke');
insert into authors (name) values ('Joshua Block');
insert into authors (name) values ('Don Roberts');