/*
 * Сумма степеней двух любых вершин графа больше или равна числу вершин графа, уменьшенному на единицу
 *  */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class SumDegVerGeqNumVer1 implements GraphProperty {
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
        Map<Integer, List<Integer>> adjacencyList = getAdjacencyList(graph);
        int n = adjacencyList.size();
        // Проверяем, есть ли хотя бы две вершины в графе
        if (n < 2)
            return false;

        // Проходим по всем парам вершин и находим сумму их степеней
        for (Map.Entry<Integer, List<Integer>> entry1 : adjacencyList.entrySet()) {
            List<Integer> neighbors1 = entry1.getValue();
            for (Map.Entry<Integer, List<Integer>> entry2 : adjacencyList.entrySet()) {
                List<Integer> neighbors2 = entry2.getValue();
                int degreeSum = neighbors1.size() + neighbors2.size(); // Сумма степеней двух вершин
                // Если сумма степеней пары вершин < n - 1, возвращаем false
                if (degreeSum < n - 1)
                    return false;
            }
        }
        return true;
    }
}