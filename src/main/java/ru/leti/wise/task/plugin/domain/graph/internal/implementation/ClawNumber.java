package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

@Component
public class ClawNumber implements GraphCharacteristic {

    @Override
    public int run(Graph graph) {
        // Создание списка смежности для заданного графа
        HashMap<Integer, List<Vertex>> adjList = createAdjList(graph);

        int clawCount = 0;

        // Перебираем все вершины графа
        for (Vertex vertex : graph.getVertexList()) {
            // Проверяем, является ли текущая вершина центром лапы
            boolean isCenter = true;
            int neighborCount = 0;
            for (Vertex neighbor : adjList.get(vertex.getId())) {
                neighborCount++;
                // Проверяем, что нет рёбер между соседними вершинами
                for (Vertex otherNeighbor : adjList.get(vertex.getId())) {
                    if (adjList.get(neighbor.getId()).contains(otherNeighbor)) {
                        isCenter = false;
                        break;
                    }
                }
                if (!isCenter) {
                    break;
                }
            }
            // Если вершина является центром лапы и имеет 3 соседа, увеличиваем счётчик лап
            if (isCenter && neighborCount == 3) {
                clawCount++;
            }
        }

        return clawCount;
    }

    // Создание списка смежности для заданного графа
    private HashMap<Integer, List<Vertex>> createAdjList(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();
        HashMap<Integer, List<Vertex>> adjList = new HashMap<>();

        // Инициализация списка смежности
        for (Vertex vertex : vertices) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        // Заполнение списка смежности рёбрами
        for (Edge edge : edges) {
            adjList.get(edge.getSource()).add(vertices.get(edge.getTarget()));
            adjList.get(edge.getTarget()).add(vertices.get(edge.getSource()));
        }
        return adjList;
    }
}
