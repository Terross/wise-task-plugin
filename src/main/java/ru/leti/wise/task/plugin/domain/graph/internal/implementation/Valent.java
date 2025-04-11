package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class Valent implements GraphProperty {

    private final List<Edge> chosenEdges = new ArrayList<>();
    private final List<Vertex> chosenVertexes = new ArrayList<>();
    private final List<Vertex> vertices = new ArrayList<>();

    private void getChosenEdges(Graph graph) {
        // Ищем рёбра красного цвета
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getColor() == Color.RED) {
                chosenEdges.add(edge);
            }
        }
    }

    private void getChosenVertexes(Graph graph) {
        // Добавляем все вершины
        vertices.addAll(graph.getVertexList());
        // Ищем вершины красного цвета
        for (Vertex vertex : vertices) {
            if (vertex.getColor() == Color.RED) {
                chosenVertexes.add(vertex);
            }
        }
    }

    private boolean isOneValent(int vertexId) {
        int from = 0;
        int to = 0;
        // Проверяем количество рёбер, входящих и исходящих из вершины
        for (Edge edge : chosenEdges) {
            if (edge.getSource() == vertexId) {
                from = from + 1;
            } else if (edge.getTarget() == vertexId) {
                to = to + 1;
            }
        }
        return from == 1 && to == 1;
    }

    @Override
    public boolean run(Graph graph) {
        // Получаем рёбра и вершины красного цвета
        getChosenEdges(graph);
        getChosenVertexes(graph);

        // Проверяем, что количество выбранных вершин соответствует количеству всех вершин графа
        if (chosenVertexes.size() != graph.getVertexCount()) {
            return false;
        }

        // Проверяем, что каждая вершина из выбранных имеет ровно одно входящее и одно исходящее ребро
        for (Vertex vertex : chosenVertexes) {
            if (!isOneValent(vertex.getId())) {
                return false;
            }
        }

        return true;
    }
}
