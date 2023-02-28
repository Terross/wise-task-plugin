CREATE USER wise_task_plugin WITH PASSWORD 'wise_task_plugin';
CREATE DATABASE wise_task_plugin WITH OWNER = wise_task_plugin;
CREATE SCHEMA wise_task_plugin;

-- docker run --name plugin
-- -e POSTGRES_PASSWORD=wise_task_plugin
-- -e POSTGRES_DB=wise_task_plugin
-- -e POSTGRES_USER=wise_task_plugin
-- -p 5432:5432
-- -d postgres
