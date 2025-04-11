package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsThreePeriodic implements GraphProperty {

    private HashMap<Integer, List<Vertex>> createAdjList(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();
        HashMap<Integer, List<Vertex>> adjList = new HashMap<>();

        // Создание пустого списка для каждой вершины
        for (var vertex : vertices) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        // Заполнение списка соседей для каждой вершины
        for (var edge : edges) {
            adjList.get(edge.getSource()).add(findVertexById(vertices, edge.getTarget()));
        }
        return adjList;
    }

    private Vertex findVertexById(List<Vertex> vertices, int id) {
        for (Vertex vertex : vertices) {
            if (vertex.getId() == id) {
                return vertex;
            }
        }
        return null;
    }

    @Override
    public boolean run(Graph graph) {
        HashMap<Integer, List<Vertex>> adjList = createAdjList(graph);

        // Проходим по всем вершинам графа
        for (Vertex vertex : graph.getVertexList()) {
            // Создаем множество для хранения цветов соседей текущей вершины
            Set<Color> colors = new HashSet<>();
            // Проходим по соседям вершины
            for (Vertex neighbor : adjList.get(vertex.getId())) {
                // Добавляем цвет соседней вершины в множество
                colors.add(neighbor.getColor());
                // Если в множестве более трех цветов, то раскраска не является 3-периодической
                if (colors.size() > 3) {
                    return false;
                }
            }
        }
        // Если все вершины прошли проверку, то раскраска является 3-периодической
        return true;
    }
}
