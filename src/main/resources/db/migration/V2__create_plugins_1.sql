INSERT INTO wise_task_plugin.plugin(id, name, category, description, plugin_type, is_internal, author_id, bean_name)
VALUES ('8ffcc670-0340-47ca-be34-ada5de6fef4b', 'Количество вершин в графе', 'Служебная', 'Количество вершин в графе',
        'GRAPH'::wise_task_plugin.plugin_type, TRUE, '00000000-0000-0000-0000-000000000000', 'vertexCount'),
       ('0e8990b4-5653-414c-b405-18b7ccbde797', 'Количество ребер в графе', 'Служебная', 'Количество ребер в графе',
        'GRAPH'::wise_task_plugin.plugin_type, TRUE, '00000000-0000-0000-0000-000000000000', 'edgeCount');