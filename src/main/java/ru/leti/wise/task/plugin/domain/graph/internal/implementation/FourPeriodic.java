package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class FourPeriodic implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

        // Строим список смежности
        for (Edge edge : graph.getEdgeList()) {
            adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge.getTarget());
            adjacencyList.computeIfAbsent(edge.getTarget(), k -> new ArrayList<>()).add(edge.getSource());
        }

        // Проверяем каждый стартовый узел с помощью DFS
        for (Vertex startVertex : vertices) {
            if (!dfsCheck(vertices, adjacencyList, startVertex.getId())) {
                return false;
            }
        }

        return true;
    }

    private boolean dfsCheck(List<Vertex> vertices, Map<Integer, List<Integer>> adjacencyList, int startVertex) {
        Stack<List<Integer>> stack = new Stack<>();
        stack.push(Collections.singletonList(startVertex));

        while (!stack.isEmpty()) {
            List<Integer> path = stack.pop();

            if (path.size() == 5) {
                int start = path.get(0);
                int end = path.get(4);

                // Сравниваем цвета вершин
                if (!vertices.get(start).getColor().equals(vertices.get(end).getColor())) {
                    return false;
                }
            } else {
                int lastVertex = path.get(path.size() - 1);
                for (int neighbor : adjacencyList.getOrDefault(lastVertex, new ArrayList<>())) {
                    if (!path.contains(neighbor)) {
                        List<Integer> newPath = new ArrayList<>(path);
                        newPath.add(neighbor);
                        stack.push(newPath);
                    }
                }
            }
        }
        return true;
    }
}
