/*
* Вершинная раскраска графа
*
* Проверяет правильную вершинную расскраску графа
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CorrectnessOfVertexColoring implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        List<Vertex> vert = graph.getVertexList();
        Map<Integer, Vertex> vertices = vert.stream()
                .collect(Collectors.toMap(Vertex::getId, v -> v));
        for (Edge edge : graph.getEdgeList()) {
            if (vertices.get(edge.getSource()).getColor() == vertices.get(edge.getTarget()).getColor()) {
                return false;
            }
        }
        return true;
    }
}