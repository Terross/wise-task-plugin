package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.mapper.PluginMapper;
import ru.leti.wise.task.plugin.repository.PluginRepository;
import ru.leti.wise.task.plugin.model.Plugin;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetPluginsOperation {

    private final PluginRepository pluginRepository;
    private final PluginMapper pluginMapper;

    public List<Plugin> activate() {
        return pluginMapper.pluginEntitiesToPlugins(pluginRepository.findAll());
    }
}
