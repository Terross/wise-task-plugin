package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.graph.GraphProperty;

@Component
public class CheckTriangulation implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        int numberOfVertices = graph.getVertexCount();
        int numberOfEdges = graph.getEdgeCount();

        if (numberOfVertices < 3) {
            return false;
        } else {
            return numberOfEdges == 3 * numberOfVertices - 6;
        }
    }
}

