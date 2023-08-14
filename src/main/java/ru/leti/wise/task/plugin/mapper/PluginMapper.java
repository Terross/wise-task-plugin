package ru.leti.wise.task.plugin.mapper;

import org.mapstruct.*;
import ru.leti.wise.task.plugin.PluginOuterClass;
import ru.leti.wise.task.plugin.PluginOuterClass.Plugin;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.PluginType;

import java.util.List;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PluginMapper {


    @Mapping(target = "jarFile", source = "jarFile", ignore = true)
    Plugin pluginEntityToPlugin(PluginEntity plugin);

    @Mapping(target = "jarFile", source = "jarFile", qualifiedByName = "jarMapping")
    PluginEntity pluginToPluginEntity(Plugin plugin);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePlugin(PluginEntity newPlugin, @MappingTarget PluginEntity oldPlugin);

    List<Plugin> pluginEntitiesToPlugins(List<PluginEntity> plugins);

    default PluginOuterClass.PluginType toPluginType(PluginType pluginType) {
        return PluginOuterClass.PluginType.valueOf(pluginType.name());
    }

    @Condition
    default boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    @Named("jarMapping")
    default byte[] toByteArray(String base64) {
        if (base64.isBlank()) {
            return null;
        }
        return decodeBase64(base64);
    }
}
