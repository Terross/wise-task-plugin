CREATE TYPE plugin_type AS ENUM ('graph');

CREATE TABLE wise_task_plugin.plugin  (
    id          UUID        NOT NULL    PRIMARY KEY,
    name        VARCHAR     NOT NULL,
    description VARCHAR     NOT NULL,
    plugin_type plugin_type NOT NULL,
    is_internal BOOLEAN     NOT NULL,
    fileName    VARCHAR,
    authorId    UUID
);