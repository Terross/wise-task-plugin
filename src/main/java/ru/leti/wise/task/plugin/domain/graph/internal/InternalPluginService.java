package ru.leti.wise.task.plugin.domain.graph.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.graph.GraphPlugin;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class InternalPluginService {

    private final Map<String, Plugin> plugins;
    private final PluginHandler graphPluginHandler;

    public <T> String run(String pluginName, Solution solution) {
        return switch (plugins.get(pluginName)) {
            case GraphPlugin p -> graphPluginHandler.run(p, solution);
            default -> throw new IllegalStateException("Unexpected value: " + plugins.get(pluginName));
        };
    }
}
