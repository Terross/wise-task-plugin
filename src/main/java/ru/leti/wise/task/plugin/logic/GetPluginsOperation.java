package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.GetAllPluginsResponse;
import ru.leti.wise.task.plugin.mapper.PluginMapper;
import ru.leti.wise.task.plugin.repository.PluginRepository;

@Component
@RequiredArgsConstructor
public class GetPluginsOperation {

    private final PluginRepository pluginRepository;
    private final PluginMapper pluginMapper;

    public GetAllPluginsResponse activate() {
        var plugins = pluginMapper.pluginEntitiesToPlugins(pluginRepository.findAll());
        return GetAllPluginsResponse.newBuilder()
                .addAllPlugin(plugins)
                .build();
    }
}
