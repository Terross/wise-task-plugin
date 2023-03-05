CREATE SCHEMA IF NOT EXISTS wise_task_plugin ;

CREATE TYPE wise_task_plugin.plugin_type AS ENUM ('GRAPH');

CREATE TABLE wise_task_plugin.plugin  (
    id          UUID        NOT NULL    PRIMARY KEY,
    name        VARCHAR     NOT NULL,
    category    VARCHAR     NOT NULL,
    description VARCHAR     NOT NULL,
    plugin_type plugin_type NOT NULL,
    is_internal BOOLEAN     NOT NULL,
    file_name   VARCHAR,
    author_id   UUID,
    bean_name   VARCHAR
);