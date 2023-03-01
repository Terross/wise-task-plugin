package ru.leti.wise.task.plugin.domain.internal.algorithm.graph;

import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

public class VertexCount implements GraphCharacteristic {
    @Override
    public int run(Graph graph) {
        return graph.getVerticesCount();
    }
}
