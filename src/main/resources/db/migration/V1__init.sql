CREATE SCHEMA IF NOT EXISTS wise_task_plugin;

CREATE TABLE wise_task_plugin.plugin
(
    id          UUID    NOT NULL PRIMARY KEY,
    name        VARCHAR NOT NULL,
    category    VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    plugin_type VARCHAR NOT NULL,
    is_internal BOOLEAN NOT NULL,
    jar_file    BYTEA,
    jar_name    VARCHAR,
    author_id   UUID,
    bean_name   VARCHAR
);