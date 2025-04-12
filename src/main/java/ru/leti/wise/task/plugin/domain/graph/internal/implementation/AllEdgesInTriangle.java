package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class AllEdgesInTriangle implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        if (graph.getEdgeCount() == 0) {
            return false;
        }

        // Получение списка смежности
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new ArrayList<>());
        }

        // Заполняем список смежности
        for (Edge edge : graph.getEdgeList()) {
            adjacencyList.get(edge.getSource()).add(edge.getTarget());
            adjacencyList.get(edge.getTarget()).add(edge.getSource());
        }

        // Проверяем, что все вершины имеют степень не менее 2
        for (Integer v : adjacencyList.keySet()) {
            if (adjacencyList.get(v).size() < 2) {
                return false; // В графе есть вершина степени 1, она не может быть частью треугольника
            }
        }

        List<Edge> visitedEdges = new ArrayList<>(); // Список посещённых рёбер
        for (Edge edge : graph.getEdgeList()) {
            int u = edge.getSource();
            int v = edge.getTarget();
            Edge currEdge = new Edge(u, v, null, null, null);

            if (!visitedEdges.contains(currEdge)) { // Если это ребро ещё не посещали, добавляем его
                visitedEdges.add(currEdge);
            } else { // Если это ребро уже встречалось, пропускаем его
                continue;
            }

            boolean isInTriangle = false;
            // Ищем, есть ли соседний для каждой вершины, чтобы образовать треугольник
            for (Integer neighbor : adjacencyList.get(u)) {
                if (adjacencyList.get(v).contains(neighbor)) {
                    isInTriangle = true;
                    Edge u_to_n = new Edge(u, neighbor, null, null, null);
                    Edge v_to_n = new Edge(v, neighbor, null, null, null);
                    // Добавляем рёбра треугольника в список посещённых
                    visitedEdges.add(u_to_n);
                    visitedEdges.add(v_to_n);
                    break;
                }
            }

            if (!isInTriangle) {
                return false; // Если не нашли треугольника, возвращаем false
            }
        }

        return true; // Все рёбра входят в треугольники
    }
}
