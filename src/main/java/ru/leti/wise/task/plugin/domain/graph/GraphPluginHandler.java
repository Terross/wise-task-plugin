package ru.leti.wise.task.plugin.domain.graph;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.plugin.graph.HandwrittenAnswer;
import ru.leti.wise.task.plugin.graph.NewGraphConstruction;

import static java.lang.String.valueOf;

@Component
@RequiredArgsConstructor
public class GraphPluginHandler implements PluginHandler {

    @Override
    public <T> String run(Plugin plugin, Graph graph, T additionalData) {
        return switch (plugin) {
            case GraphProperty p -> valueOf(p.run(graph));
            case GraphCharacteristic p -> valueOf(p.run(graph));
            case HandwrittenAnswer p -> valueOf(p.run(graph, (String) additionalData));
            case NewGraphConstruction p -> valueOf(p.run(graph, (Graph) additionalData));
            default -> throw new IllegalStateException("Unexpected value: " + plugin);
        };
    }
}
