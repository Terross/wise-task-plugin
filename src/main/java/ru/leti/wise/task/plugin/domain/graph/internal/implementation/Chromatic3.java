package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class Chromatic3 implements GraphProperty {

    // Создаёт список смежности для графа
    private Map<Integer, List<Integer>> createAdjacencyList(Graph graph) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new ArrayList<>());
        }
        for (Edge edge : graph.getEdgeList()) {
            adjacencyList.get(edge.getSource()).add(edge.getTarget());
            adjacencyList.get(edge.getTarget()).add(edge.getSource());
        }
        return adjacencyList;
    }

    // Устанавливает всем вершинам указанный цвет
    private void setAllVerticesColor(Graph graph, Color color) {
        for (Vertex vertex : graph.getVertexList()) {
            vertex.setColor(color);
        }
    }

    // Вычисляет степени всех вершин
    private Map<Integer, Integer> calculateDegrees(Map<Integer, List<Integer>> adjacencyList) {
        Map<Integer, Integer> degrees = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : adjacencyList.entrySet()) {
            degrees.put(entry.getKey(), entry.getValue().size());
        }
        return degrees;
    }

    @Override
    public boolean run(Graph graph) {
        // Устанавливаем всем вершинам цвет, который при раскраске использовать не будем
        setAllVerticesColor(graph, Color.RED); // Аналогично старому Color.yellow

        // Создание списка смежности и вычисление степеней для каждой вершины
        Map<Integer, List<Integer>> adjacencyList = createAdjacencyList(graph);
        Map<Integer, Integer> degrees = calculateDegrees(adjacencyList);

        // Получаем отсортированный список вершин по убыванию степени
        List<Integer> sortedVertices = new ArrayList<>(degrees.keySet());
        sortedVertices.sort((v1, v2) -> Integer.compare(degrees.get(v2), degrees.get(v1)));

        // Используем массив из 3 цветов для раскраски
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
        Set<Color> usedColors = new HashSet<>();

        // Раскраска вершин
        for (Integer vertexId : sortedVertices) {
            Set<Color> neighborColors = new HashSet<>();
            for (Integer neighborId : adjacencyList.get(vertexId)) {
                neighborColors.add(graph.getVertexList().get(neighborId).getColor());
            }
            Color availableColor = Color.RED; // Аналогично старому Color.yellow

            // Находим первый доступный цвет для текущей вершины
            for (Color color : colors) {
                if (!neighborColors.contains(color)) {
                    availableColor = color;
                    break;
                }
            }

            // Если вершина осталась с начальным цветом, значит не удалось раскрасить граф
            if (availableColor == Color.RED) {
                return false;
            }

            // Назначаем цвет вершине
            graph.getVertexList().get(vertexId).setColor(availableColor);
            usedColors.add(availableColor);
        }

        // Проверяем, использованы ли все три цвета
        return usedColors.size() == 3;
    }
}
