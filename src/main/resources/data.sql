
INSERT INTO teams (name, version) VALUES ('Marketing', 0);
INSERT INTO teams (name, version) VALUES ('Engineering', 0);

-- 2. Create Step Counters (Lowercase 'step_counters')
INSERT INTO step_counters (name, steps, state, version) VALUES ('Marketing Counter', 0, 'ENABLED', 0);
INSERT INTO step_counters (name, steps, state, version) VALUES ('Engineering Counter', 1500, 'ENABLED', 0);

-- 3. Link Teams to Counters
UPDATE teams SET step_counter_id = 1 WHERE id = 1;
UPDATE teams SET step_counter_id = 2 WHERE id = 2;

-- Link the counters back to the teams so the relationship is complete
UPDATE step_counters SET team_id = 1 WHERE id = 1;
UPDATE step_counters SET team_id = 2 WHERE id = 2;

INSERT INTO employees (first_name, last_name, team_id, version) VALUES ('Alice', 'Smith', 1, 0);
INSERT INTO employees (first_name, last_name, team_id, version) VALUES ('Bob', 'Jones', 1, 0);
INSERT INTO employees (first_name, last_name, team_id, version) VALUES ('Charlie', 'Brown', 2, 0);