package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class TriangleDegreeThreeChecker implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        Map<Integer, Integer> vertexDegrees = calculateVertexDegrees(graph);
        List<Set<Integer>> triangles = findTriangles(graph);

        for (Set<Integer> triangle : triangles) {
            int degreeThreeCount = 0;
            for (Integer vertex : triangle) {
                if (vertexDegrees.get(vertex) >= 3) {
                    degreeThreeCount++;
                }
            }
            if (degreeThreeCount < 2) {
                return false;
            }
        }
        return true;
    }

    private Map<Integer, Integer> calculateVertexDegrees(Graph graph) {
        Map<Integer, Integer> vertexDegrees = new HashMap<>();
        for (Edge edge : graph.getEdgeList()) {
            int fromV = edge.getSource();
            int toV = edge.getTarget();
            vertexDegrees.put(fromV, vertexDegrees.getOrDefault(fromV, 0) + 1);
            vertexDegrees.put(toV, vertexDegrees.getOrDefault(toV, 0) + 1);
        }
        return vertexDegrees;
    }

    private List<Set<Integer>> findTriangles(Graph graph) {
        List<Set<Integer>> triangles = new ArrayList<>();
        Map<Integer, Set<Integer>> adjacencyList = new HashMap<>();

        for (Edge edge : graph.getEdgeList()) {
            int fromV = edge.getSource();
            int toV = edge.getTarget();
            adjacencyList.putIfAbsent(fromV, new HashSet<>());
            adjacencyList.putIfAbsent(toV, new HashSet<>());
            adjacencyList.get(fromV).add(toV);
            adjacencyList.get(toV).add(fromV);
        }

        for (Integer vertex : adjacencyList.keySet()) {
            Set<Integer> neighbors = adjacencyList.get(vertex);
            for (Integer neighbor1 : neighbors) {
                for (Integer neighbor2 : neighbors) {
                    if (neighbor1.equals(neighbor2)) continue; // Пропускаем, если это одна и та же вершина
                    if (adjacencyList.get(neighbor1).contains(neighbor2)) {
                        Set<Integer> triangle = new HashSet<>();
                        triangle.add(vertex);
                        triangle.add(neighbor1);
                        triangle.add(neighbor2);
                        // Проверка на уникальность треугольника
                        if (!triangles.contains(triangle)) {
                            triangles.add(triangle);
                        }
                    }
                }
            }
        }
        return triangles;
    }
}
