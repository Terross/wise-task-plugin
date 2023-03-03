package ru.leti.wise.task.plugin.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "plugin")
public class PluginEntity {

    @Id
    private UUID id;

    private String name;

    private String category;

    private String fileName;

    private String description;

    private UUID authorId;

    @Column(name = "is_internal")
    private Boolean isInternal;

    @Column(name = "bean_name")
    private String beanName;

    @Column(name = "plugin_type")
    @Enumerated(STRING)
    private PluginType pluginType;
}
