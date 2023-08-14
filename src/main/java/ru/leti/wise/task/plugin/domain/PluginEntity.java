package ru.leti.wise.task.plugin.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

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

    private String description;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "is_internal")
    private Boolean isInternal;

    @Column(name = "bean_name")
    private String beanName;

    @Column(name = "plugin_type")
    @Enumerated(STRING)
    private PluginType pluginType;

    @Lob
    @JdbcType(BinaryJdbcType.class)
    @Column(name = "jar_file")
    private byte[] jarFile;

    @Column(name = "jar_name")
    private String jarName;
}
