package ru.leti.wise.task.plugin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import ru.leti.wise.task.plugin.PluginOuterClass;
import ru.leti.wise.task.plugin.PluginOuterClass.Plugin;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.PluginType;

import java.util.List;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PluginMapper {


    Plugin pluginEntityToPlugin(PluginEntity plugin);

    PluginEntity pluginToPluginEntity(Plugin plugin);

    List<Plugin> pluginEntitiesToPlugins(List<PluginEntity> plugins);

    default PluginOuterClass.PluginType toPluginType(PluginType pluginType) {
        return PluginOuterClass.PluginType.valueOf(pluginType.name());
    }
}
