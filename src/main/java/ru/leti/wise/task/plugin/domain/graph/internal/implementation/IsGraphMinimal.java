package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import java.util.*;

@Component
public class IsGraphMinimal {

    // Проходит по всем рёбрам графа, временно удаляет каждое из них и проверяет, остаётся ли граф связным после удаления
    public boolean execute(Graph graph) {
        List<Edge> edges = graph.getEdgeList();
        for (Edge edge : edges) {
            Graph graphCopy = removeEdge(graph, edge);
            if (isConnected(graphCopy)) {
                return false;
            }
        }
        return true;
    }

    // Создаёт копию исходного графа, удаляет из копии указанное ребро и возвращает изменённую копию графа.
    private Graph removeEdge(Graph graph, Edge edgeToRemove) {
        // Создаём новый граф, копируя все данные из старого
        Graph newGraph = new Graph(
                graph.getVertexCount(),
                graph.getEdgeCount(),
                graph.isDirect(),
                new ArrayList<>(graph.getEdgeList()),
                new ArrayList<>(graph.getVertexList())
        );

        // Удаляем ребро из копии
        newGraph.getEdgeList().remove(edgeToRemove);

        return newGraph;
    }

    // Проверяет, является ли граф связным
    public static boolean isConnected(Graph graph) {
        if (graph.getVertexList().isEmpty()) {
            return false;
        }

        int startVertexId = graph.getVertexList().get(0).getId(); // Берём id первой вершины


        Set<Integer> visited = depthFirstSearch(graph, startVertexId);

        // Проверяем, все ли вершины были посещены
        for (var vertex : graph.getVertexList()) {
            if (!visited.contains(vertex.getId())) {
                return false;
            }
        }

        return true;
    }

    public static Set<Integer> depthFirstSearch(Graph graph, int startVertexId) {
        Set<Integer> visitedSet = new HashSet<>();
        depthFirstSearchUtil(graph, startVertexId, visitedSet);
        return visitedSet;
    }

    private static void depthFirstSearchUtil(Graph graph, int vertexId, Set<Integer> visitedSet) {
        visitedSet.add(vertexId);

        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == vertexId && !visitedSet.contains(edge.getTarget())) {
                depthFirstSearchUtil(graph, edge.getTarget(), visitedSet);
            }
            if (edge.getTarget() == vertexId && !visitedSet.contains(edge.getSource())) {
                depthFirstSearchUtil(graph, edge.getSource(), visitedSet);
            }
        }
    }
}
