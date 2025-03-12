/*
* Хроматическое число графа равно двум
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class ChromaticNumber2 implements GraphProperty {
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
        // Граф без рёбер имеет хроматическое число 1
        if (graph.getEdgeCount() == 0)
            return false;

        Color defaultColor = Color.GRAY;
        Color firstColor = Color.RED;
        Color secondColor = Color.BLUE;

        // Словарь раскраски вершин (эта раскраска не коррелирует с исходной раскраской вершин)
        Map<Integer, Color> colorMap = new HashMap<>();

        // Покрасим все вершины в один цвет перед началом работы алгоритма
        graph.getVertexList().forEach(vertex -> colorMap.put(vertex.getId(), defaultColor));

        Map<Integer, List<Integer>> adjacencyList = getAdjacencyList(graph);

        for (var entry : colorMap.entrySet()) {
            if (!entry.getValue().equals(defaultColor))
                continue;

            Queue<Integer> queue = new LinkedList<>();
            colorMap.put(entry.getKey(), firstColor);
            queue.add(entry.getKey());

            while (!queue.isEmpty()) {
                int current = queue.poll();
                List<Integer> adjacentVertices = adjacencyList.get(current);
                Color currentVertexColor = colorMap.get(current);
                Color colorToPaint = currentVertexColor == firstColor ? secondColor : firstColor;

                for (int vertex : adjacentVertices) {
                    if (colorMap.get(vertex).equals(defaultColor)) {
                        colorMap.put(vertex, colorToPaint);
                        queue.add(vertex);
                    } else if (colorMap.get(vertex).equals(currentVertexColor))
                        return false;
                }
            }
        }
        return true;
    }
}