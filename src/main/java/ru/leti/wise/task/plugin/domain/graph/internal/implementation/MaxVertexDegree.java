package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

@Component
public class MaxVertexDegree implements GraphCharacteristic {

    private HashMap<Integer, List<Vertex>> createAdjList(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();
        HashMap<Integer, List<Vertex>> adjList = new HashMap<>();

        // Создаём список смежности для каждой вершины
        for (Vertex vertex : vertices) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        // Добавляем соседей для каждой вершины
        for (Edge edge : edges) {
            Vertex fromVertex = vertices.stream().filter(v -> v.getId() == edge.getSource()).findFirst().orElse(null);
            Vertex toVertex = vertices.stream().filter(v -> v.getId() == edge.getTarget()).findFirst().orElse(null);
            if (fromVertex != null && toVertex != null) {
                adjList.get(edge.getSource()).add(toVertex);
                adjList.get(edge.getTarget()).add(fromVertex);
            }
        }

        return adjList;
    }

    @Override
    public int run(Graph graph) {
        HashMap<Integer, List<Vertex>> adjList = createAdjList(graph);
        int maxDegree = 0;

        // Ищем максимальную степень среди всех вершин
        for (List<Vertex> neighbours : adjList.values()) {
            maxDegree = Math.max(maxDegree, neighbours.size());
        }
        return maxDegree;
    }
}
