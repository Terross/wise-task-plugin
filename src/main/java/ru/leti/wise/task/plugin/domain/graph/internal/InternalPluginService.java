package ru.leti.wise.task.plugin.domain.graph.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.domain.graph.PluginService;
import ru.leti.wise.task.plugin.graph.GraphPlugin;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class InternalPluginService implements PluginService {

    private final Map<String, Plugin> plugins;
    private final PluginHandler graphPluginHandler;

    public String run(PluginEntity plugin, Solution solution) {
        var pluginName = plugin.getBeanName();
        if (plugins.get(pluginName) instanceof GraphPlugin p) {
            return graphPluginHandler.run(p, solution);
        } else {
            throw new IllegalStateException("Unexpected value: " + plugins.get(pluginName));
        }
    }
}
