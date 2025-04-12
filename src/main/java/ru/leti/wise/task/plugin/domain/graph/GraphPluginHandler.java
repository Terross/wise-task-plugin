package ru.leti.wise.task.plugin.domain.graph;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.plugin.graph.HandwrittenAnswer;
import ru.leti.wise.task.plugin.graph.NewGraphConstruction;
import ru.leti.wise.task.plugin.mapper.GraphMapper;

import static java.lang.String.valueOf;

@Component
@RequiredArgsConstructor
public class GraphPluginHandler implements PluginHandler {

    private final GraphMapper graphMapper;

    @Override
    public String run(Plugin plugin, Solution solution) {
        if (solution.getPayloadCase() != Solution.PayloadCase.GRAPH) {
            throw new RuntimeException("Inconsistent graph plugin answer");
        }
        var graph = graphMapper.toGraph(solution.getGraph());
        if (plugin instanceof GraphProperty p) {
            return  valueOf(p.run(graph));
        } else if (plugin instanceof GraphCharacteristic p) {
            return valueOf(p.run(graph));
        } else if (plugin instanceof HandwrittenAnswer p) {
            return valueOf(p.run(graph, solution.getHandwrittenAnswer()));
        } else if (plugin instanceof NewGraphConstruction p) {
            return valueOf(p.run(graph, graphMapper.toGraph(solution.getOtherGraph())));
        } else {
            throw new IllegalStateException("Unexpected value: " + plugin);
        }
    }
}
