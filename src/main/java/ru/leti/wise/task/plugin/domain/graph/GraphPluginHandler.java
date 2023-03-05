package ru.leti.wise.task.plugin.domain.graph;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.model.Graph;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.plugin.graph.HandwrittenAnswer;
import ru.leti.wise.task.plugin.graph.NewGraphConstruction;
import ru.leti.wise.task.plugin.mapper.GraphMapper;
import ru.leti.wise.task.plugin.model.RequestPayloadAdditionalData;
import ru.leti.wise.task.plugin.model.RequestPayloadMainPayload;

import static java.lang.String.valueOf;

@Component
@RequiredArgsConstructor
public class GraphPluginHandler implements PluginHandler {
    
    private final GraphMapper graphMapper;

    @Override
    public String run(Plugin plugin, RequestPayloadMainPayload mainPayload, RequestPayloadAdditionalData additionalData) {
        var graph = graphMapper.graphRequestToGraph((Graph) mainPayload);
        return switch (plugin) {
            case GraphProperty p -> valueOf(p.run(graph));
            case GraphCharacteristic p -> valueOf(p.run(graph));
            case HandwrittenAnswer p -> valueOf(p.run(graph, ((ru.leti.wise.task.plugin.model.HandWrittenAnswer) additionalData).getData()));
            case NewGraphConstruction p -> valueOf(p.run(graph, graphMapper.graphRequestToGraph((Graph) additionalData)));
            default -> throw new IllegalStateException("Unexpected value: " + plugin);
        };
    }
}
