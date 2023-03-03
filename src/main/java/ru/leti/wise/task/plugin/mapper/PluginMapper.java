package ru.leti.wise.task.plugin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.model.Plugin;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PluginMapper {

    Plugin pluginEntityToPlugin(PluginEntity plugin);

    List<Plugin> pluginEntitiesToPlugins(List<PluginEntity> plugins);
}
