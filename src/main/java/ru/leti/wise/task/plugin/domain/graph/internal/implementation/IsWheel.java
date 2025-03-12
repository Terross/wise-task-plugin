/*
* Граф - колесо
*
* В теории графов колесом называется граф с n вершинами (n ≥ 4),
* образованный соединением единственной вершины со всеми вершинами (n-1)-цикла
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsWheel implements GraphProperty {
    public Map<Integer, List<Integer>> getAdjacencyList(Graph graph) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        graph.getVertexList().forEach(vertex -> adjacencyList.put(vertex.getId(), new ArrayList<>()));

        for (Edge edge : graph.getEdgeList()) {
            int key = edge.getSource();
            int value = edge.getTarget();
            adjacencyList.get(key).add(value);
            if (!graph.isDirect())
                adjacencyList.get(value).add(key);
        }

        return adjacencyList;
    }

    @Override
    public boolean run(Graph graph) {
        if (graph.getEdgeCount() < 4) {
            return false;
        }
        Map<Integer, List<Integer>> adjacencyList = getAdjacencyList(graph);
        int n = adjacencyList.size();

        // Находим центр и его соседей
        Integer center = null;
        for (Map.Entry<Integer, List<Integer>> entry : adjacencyList.entrySet()) {
            int vertex = entry.getKey();
            List<Integer> neighbors = entry.getValue();
            if (neighbors.size() == n - 1) {
                center = vertex;
                break;
            }
        }
        // Если не найдена вершина, соединенная со всеми остальными, граф не является колесом
        if (center == null)
            return false;

        // Проверяем, что все остальные вершины соединены с центром и ещё с двумя другоими вершинами
        for (Map.Entry<Integer, List<Integer>> entry : adjacencyList.entrySet()) {
            int vertex = entry.getKey();
            List<Integer> neighbors = entry.getValue();
            if (vertex != center) {
                if (neighbors.size() != 3 || !neighbors.contains(center))
                    return false;
            }
        }

        return true;
    }
}