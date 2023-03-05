package ru.leti.wise.task.plugin.domain.graph.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.graph.GraphPlugin;
import ru.leti.wise.task.plugin.model.RequestPayloadAdditionalData;
import ru.leti.wise.task.plugin.model.RequestPayloadMainPayload;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class InternalPluginService {

    private final Map<String, Plugin> plugins;
    private final PluginHandler graphPluginHandler;

    public String run(String pluginName, RequestPayloadMainPayload mainPayload, RequestPayloadAdditionalData additionalData) {
        return switch (plugins.get(pluginName)) {
            case GraphPlugin p -> graphPluginHandler.run(p, mainPayload, additionalData);
            default -> throw new IllegalStateException("Unexpected value: " + plugins.get(pluginName));
        };
    }
}
