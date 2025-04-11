package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class VertexColoring2Periodic implements GraphProperty {

    private HashMap<Integer, List<Vertex>> createAdjList(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();
        HashMap<Integer, List<Vertex>> adjList = new HashMap<>();

        // Инициализируем список соседей для каждой вершины
        for (var vertex : vertices) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        // Заполняем список соседей на основе рёбер
        for (var edge : edges) {
            int from = edge.getSource();
            int to = edge.getTarget();
            adjList.get(from).add(findVertexById(vertices, to));
            adjList.get(to).add(findVertexById(vertices, from));
        }

        return adjList;
    }

    // Метод для поиска вершины по её идентификатору
    private Vertex findVertexById(List<Vertex> vertices, int id) {
        for (Vertex vertex : vertices) {
            if (vertex.getId() == id) {
                return vertex;
            }
        }
        return null;  // Если вершина не найдена (не должно быть такого случая)
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
                // Если в множестве более двух цветов, то раскраска не является 2-периодической
                if (colors.size() > 2) {
                    return false;
                }
            }
        }
        // Если все вершины прошли проверку, то раскраска является 2-периодической
        return true;
    }
}
