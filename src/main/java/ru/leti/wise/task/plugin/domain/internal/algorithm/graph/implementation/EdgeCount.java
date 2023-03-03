package ru.leti.wise.task.plugin.domain.internal.algorithm.graph.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

@Component
public class EdgeCount implements GraphCharacteristic {
    @Override
    public int run(Graph graph) {
        return graph.getEdgeCount();
    }
}
