package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

@Component
public class SizeOfMinSetSepsVertices implements GraphCharacteristic {

    // Создание списка смежности для заданного графа
    private HashMap<Integer, List<Vertex>> createAdjList(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();
        HashMap<Integer, List<Vertex>> adjList = new HashMap<>();
        for (Vertex vertex : vertices) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : edges) {
            adjList.get(edge.getSource()).add(getVertexById(vertices, edge.getTarget()));
        }
        return adjList;
    }

    @Override
    public int run(Graph graph) {
        Vertex startVertex = null;
        Vertex finishVertex = null;

        // Находим начало и конец пути
        for (Vertex vertex : graph.getVertexList()) {
            if (Color.BLUE.equals(vertex.getColor())) {
                startVertex = vertex;
            }
            if (Color.RED.equals(vertex.getColor())) {
                finishVertex = vertex;
            }
            if (startVertex != null && finishVertex != null) {
                break;
            }
        }

        // Обрабатываем случай, когда вершины не найдены
        if (startVertex == null || finishVertex == null) {
            return -1;
        }

        List<Vertex> vertices = graph.getVertexList();
        // Создаем хешмапу посещенных вершин
        Map<Integer, Boolean> visited = new HashMap<>();
        for (Vertex vertex : vertices) {
            visited.put(vertex.getId(), false);
        }

        Map<Integer, List<Vertex>> adjList = createAdjList(graph);

        // Вычитаем 1 для того, чтобы вывести количество вершин на пути, а не рёбер
        int result = bfs(startVertex, finishVertex, vertices, visited, adjList);
        return result > 0 ? result - 1 : -1;
    }

    private Integer bfs(Vertex start, Vertex finish, List<Vertex> vertices, Map<Integer, Boolean> visited, Map<Integer, List<Vertex>> adjList) {
        visited.put(start.getId(), true);
        List<Vertex> queue = new ArrayList<>();
        HashMap<Vertex, Integer> length = new HashMap<>();
        for (Vertex vertex : vertices) {
            length.put(vertex, -1);
        }
        length.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Vertex now = queue.remove(0);
            for (Vertex neighbour : adjList.get(now.getId())) {
                if (!visited.get(neighbour.getId())) {
                    visited.put(neighbour.getId(), true);
                    queue.add(neighbour);
                    length.put(neighbour, length.get(now) + 1);
                }
            }
        }
        return length.get(finish);
    }

    private Vertex getVertexById(List<Vertex> vertices, int id) {
        for (Vertex vertex : vertices) {
            if (vertex.getId() == id) {
                return vertex;
            }
        }
        return null;
    }
}
