package ru.leti.wise.task.plugin.domain.internal.algorithm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.graph.GraphPlugin;
import ru.leti.wise.task.plugin.mapper.GraphMapper;
import ru.leti.wise.task.plugin.model.Graph;
import ru.leti.wise.task.plugin.model.RequestPayloadAdditionalData;
import ru.leti.wise.task.plugin.model.RequestPayloadMainPayload;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class InternalPluginService {

    private final Map<String, Plugin> plugins;
    private final InternalPluginHandler internalGraphPluginHandler;
    private final GraphMapper graphMapper;

    public String run(String pluginName, RequestPayloadMainPayload mainPayload, RequestPayloadAdditionalData additionalData) {
        return switch (plugins.get(pluginName)) {
            case GraphPlugin p -> internalGraphPluginHandler.run(p, graphMapper.graphRequestToGraph((Graph) mainPayload), additionalData);
            default -> throw new IllegalStateException("Unexpected value: " + plugins.get(pluginName));
        };
    }
}
