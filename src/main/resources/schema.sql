-- Disable foreign key checks to allow dropping tables in any order
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS teams;
DROP TABLE IF EXISTS step_counters;

CREATE TABLE employees (
                           id BIGINT NOT NULL NOT NULL GENERATED ALWAYS AS IDENTITY, -- Standard 2026 syntax
                           first_name VARCHAR(255) NOT NULL,
                           last_name VARCHAR(255) NOT NULL,
                           team_id BIGINT,
                           version INTEGER DEFAULT 0, -- Required for JPA @Version and cache consistency
                           PRIMARY KEY (id),
                           CONSTRAINT fk_employee_team FOREIGN KEY (team_id) REFERENCES teams(id)
                               ON DELETE SET NULL
);

CREATE TABLE step_counters (
                               id BIGINT NOT NULL NOT NULL GENERATED ALWAYS AS IDENTITY, -- Standard 2026 syntax
                               name VARCHAR(255) NOT NULL,
                               steps INTEGER DEFAULT 0,
                               version INTEGER DEFAULT 0,
                               team_id BIGINT UNIQUE, -- UNIQUE ensures the 1:1 relationship with Teams
                               state ENUM('ENABLED', 'DISABLED') NOT NULL DEFAULT 'ENABLED',
                               PRIMARY KEY (id),
                               CONSTRAINT fk_step_counter_team FOREIGN KEY (team_id) REFERENCES teams(id)
);

CREATE TABLE teams (
                       id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY, -- Standard 2026 syntax
                       name VARCHAR(255) NOT NULL,
                       version INTEGER DEFAULT 0,
                       step_counter_id BIGINT,
                       PRIMARY KEY (id)
);
-- Re-enable foreign key checks for table creation
SET FOREIGN_KEY_CHECKS = 1;