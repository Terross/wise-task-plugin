package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class TriangleChecker implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        for (Edge edge : graph.getEdgeList()) {
            Vertex vertex1 = getVertexById(graph, edge.getSource());
            Vertex vertex2 = getVertexById(graph, edge.getTarget());

            Set<Vertex> commonNeighbors = findCommonNeighbors(graph, vertex1, vertex2);

            for (Vertex commonNeighbor : commonNeighbors) {
                int res_count = 0;
                if (degreeIsAtLeastTwo(graph, vertex1)) res_count++;
                if (degreeIsAtLeastTwo(graph, vertex2)) res_count++;
                if (degreeIsAtLeastTwo(graph, commonNeighbor)) res_count++;
                if (res_count < 2) {
                    return false;
                }
            }
        }
        return true;
    }

    private Vertex getVertexById(Graph graph, int id) {
        return graph.getVertexList().stream()
                .filter(vertex -> vertex.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private Set<Vertex> findCommonNeighbors(Graph graph, Vertex vertex1, Vertex vertex2) {
        Set<Vertex> commonNeighbors = new HashSet<>();
        Set<Vertex> neighborsOfVertex1 = getNeighbors(graph, vertex1);
        Set<Vertex> neighborsOfVertex2 = getNeighbors(graph, vertex2);

        for (Vertex neighbor : neighborsOfVertex1) {
            if (neighborsOfVertex2.contains(neighbor)) {
                commonNeighbors.add(neighbor);
            }
        }
        return commonNeighbors;
    }

    private Set<Vertex> getNeighbors(Graph graph, Vertex vertex) {
        Set<Vertex> neighbors = new HashSet<>();
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == vertex.getId()) {
                neighbors.add(getVertexById(graph, edge.getTarget()));
            } else if (edge.getTarget() == vertex.getId()) {
                neighbors.add(getVertexById(graph, edge.getSource()));
            }
        }
        return neighbors;
    }

    private boolean degreeIsAtLeastTwo(Graph graph, Vertex vertex) {
        int degree = (int) graph.getEdgeList().stream()
                .filter(edge -> edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId())
                .count();
        return degree >= 2;
    }
}
