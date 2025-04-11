package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

@Component
public class MinNodeDegree implements GraphCharacteristic {
    @Override
    public int run(Graph graph) {
        // check if graph has no vertices
        if (graph.getVertexCount() == 0)
            return -1;
        var adjacencyList = getAdjacencyList(graph);
        return getMinDegree(adjacencyList);
    }

    private int getMinDegree(Map<Integer, List<Edge>> adjacencyList) {
        return adjacencyList.values().stream()
                .mapToInt(List::size)
                .min()
                .orElse(0);
    }

    private Map<Integer, List<Edge>> getAdjacencyList(Graph graph) {
        var adjacencyList = new HashMap<Integer, List<Edge>>();
        for (Vertex vertex : graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new ArrayList<>());
        }
        for (Edge edge : graph.getEdgeList()) {
            int source = edge.getSource(), target = edge.getTarget();
            adjacencyList.get(source).add(edge);
            adjacencyList.get(target).add(edge);
        }
        return adjacencyList;
    }
}
