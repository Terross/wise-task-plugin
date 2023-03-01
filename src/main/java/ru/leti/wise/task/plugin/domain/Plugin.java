package ru.leti.wise.task.plugin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "plugin")
public class Plugin {

    @Id
    private UUID id;

    private String name;

    private String fileName;

    private String description;

    private UUID authorId;

    @Column(name = "is_internal")
    private Boolean isInternal;

    @Column(name = "plugin_type")
    private PluginType pluginType;
}
