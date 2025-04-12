import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

public class MarkedVertexTheGraphBarrier implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        if (graph.getVertexList() == null || graph.getEdgeList() == null) {
            throw new IllegalArgumentException("Граф не содержит вершин или рёбер");
        }
        return isBarrier(graph);
    }

    private boolean isBarrier(Graph graph) {
        int numSelectedVertex = 0;
        for (Vertex vertex : graph.getVertexList()) {
            if (vertex.getColor() != Color.BLUE) { //был сервый цвет -- заменила на синий
                numSelectedVertex++;
            }
        }
        return (countOddComponents(graph) - numSelectedVertex) == numOfFreeVertex(graph);
    }

    /**
     * Подсчёт количества несвязанных вершин
     */
    private int numOfFreeVertex(Graph graph) {
        Set<Integer> verticesWithEdges = new HashSet<>();

        for (Edge edge : graph.getEdgeList()) {
            verticesWithEdges.add(edge.getSource());
            verticesWithEdges.add(edge.getTarget());
        }

        int isolatedVerticesCount = 0;
        for (Vertex vertex : graph.getVertexList()) {
            if (!verticesWithEdges.contains(vertex.getId())) {
                isolatedVerticesCount++;
            }
        }
        return isolatedVerticesCount;
    }

    private static int countOddComponents(Graph graph) {
        Map<Integer, Vertex> selectedVertices = new HashMap<>();

        for (Vertex vertex : graph.getVertexList()) {
            if (vertex.getColor() == Color.BLUE) {
                selectedVertices.put(vertex.getId(), vertex);
            }
        }

        List<Edge> unselectedEdges = new ArrayList<>();
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getColor() == Color.BLUE) {
                unselectedEdges.add(edge);
            }
        }

        Map<Integer, List<Integer>> adjacencyList = buildAdjacencyList(unselectedEdges);
        Set<Integer> visited = new HashSet<>();
        int oddComponentCount = 0;

        for (int vertexId : selectedVertices.keySet()) {
            if (!visited.contains(vertexId)) {
                int componentSize = bfsComponentSize(vertexId, adjacencyList, visited);
                if (componentSize % 2 != 0) {
                    oddComponentCount++;
                }
            }
        }

        return oddComponentCount;
    }

    private static Map<Integer, List<Integer>> buildAdjacencyList(List<Edge> edges) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        for (Edge edge : edges) {
            adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge.getTarget());
            adjacencyList.computeIfAbsent(edge.getTarget(), k -> new ArrayList<>()).add(edge.getSource());
        }
        return adjacencyList;
    }

    private static int bfsComponentSize(int startVertex, Map<Integer, List<Integer>> adjacencyList, Set<Integer> visited) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startVertex);
        visited.add(startVertex);
        int size = 0;

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            size++;
            for (int neighbor : adjacencyList.getOrDefault(vertex, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return size;
    }
}
