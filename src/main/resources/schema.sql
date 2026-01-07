-- Disable foreign key checks to allow dropping tables in any order
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS teams;
DROP TABLE IF EXISTS step_counters;

create table employees (version integer, id bigint not null auto_increment, team_id bigint, first_name varchar(255), last_name varchar(255), primary key (id));

create table step_counters (steps integer, version integer, id bigint not null auto_increment, team_id bigint, name varchar(255), state enum ('DISABLED','ENABLED'), primary key (id));

create table teams (version integer, id bigint not null auto_increment, step_counter_id bigint, name varchar(255), primary key (id));

-- Re-enable foreign key checks for table creation
SET FOREIGN_KEY_CHECKS = 1;