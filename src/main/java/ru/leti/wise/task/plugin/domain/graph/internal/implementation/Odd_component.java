package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;


@Component
public class Odd_component implements GraphCharacteristic {

    @Override
    public int run(Graph graph) {
        Set<Vertex> visited = new HashSet<>();
        int oddComponentCount = 0;

        for (Vertex vertex : graph.getVertexList()) {
            if (!visited.contains(vertex)) {
                Set<Vertex> component = new HashSet<>();
                dfs(vertex, visited, component, graph);
                if (component.size() % 2 != 0) {
                    oddComponentCount++;
                }
            }
        }

        return oddComponentCount;
    }

    private void dfs(Vertex vertex, Set<Vertex> visited, Set<Vertex> component, Graph graph) {
        visited.add(vertex);
        component.add(vertex);

        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == vertex.getId()) {
                Vertex neighbor = graph.getVertexList().stream()
                        .filter(v -> v.getId() == edge.getTarget())
                        .findFirst()
                        .orElse(null);
                if (neighbor != null && !visited.contains(neighbor)) {
                    dfs(neighbor, visited, component, graph);
                }
            } else if (edge.getTarget() == vertex.getId()) {
                Vertex neighbor = graph.getVertexList().stream()
                        .filter(v -> v.getId() == edge.getSource())
                        .findFirst()
                        .orElse(null);
                if (neighbor != null && !visited.contains(neighbor)) {
                    dfs(neighbor, visited, component, graph);
                }
            }
        }
    }
}
