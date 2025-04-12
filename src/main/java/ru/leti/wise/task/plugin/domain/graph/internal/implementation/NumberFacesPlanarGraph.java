package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class NumberFacesPlanarGraph implements GraphCharacteristic {

    private int vertexCount;
    private ArrayList<ArrayList<Integer>> adjacencyMatrix;

    @Override
    public int run(Graph graph) {
        if (!isPlanar(graph)) {
            throw new IllegalArgumentException("Граф должен быть планарным");
        }

        if (graph.getVertexList() == null || graph.getEdgeList() == null) {
            throw new IllegalArgumentException("Граф не содержит вершин или ребер");
        }

        int edgeCount = graph.getEdgeCount();
        int vertexCount = graph.getVertexCount();

        if (edgeCount < 3 || vertexCount < 3) {
            throw new IllegalArgumentException("Граф должен содержать как минимум 3 вершины и 3 ребра");
        }

        if (!isConnectedGraph(graph)) {
            throw new IllegalArgumentException("Граф не является связным");
        }

        return edgeCount - vertexCount + 2;
    }

    private boolean isConnectedGraph(Graph graph) {
        Map<Integer, Boolean> visited = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            visited.put(vertex.getId(), false);
        }

        dfs(graph, graph.getVertexList().get(0).getId(), visited);

        return !visited.containsValue(false);
    }

    private void dfs(Graph graph, int currentVertexId, Map<Integer, Boolean> visited) {
        visited.put(currentVertexId, true);
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == currentVertexId && !visited.get(edge.getTarget())) {
                dfs(graph, edge.getTarget(), visited);
            }
            if (edge.getTarget() == currentVertexId && !visited.get(edge.getSource())) {
                dfs(graph, edge.getSource(), visited);
            }
        }
    }

    private boolean isPlanar(Graph graph) {
        setValues(graph);

        if (vertexCount <= 4) {
            return true;
        }

        return !(checkForK5() || checkForK33());
    }

    private void setValues(Graph graph) {
        vertexCount = graph.getVertexCount();
        int edgeCount = graph.getEdgeCount();
        List<Edge> edges = graph.getEdgeList();
        List<Integer> vertices = new ArrayList<>();
        adjacencyMatrix = new ArrayList<>(vertexCount);

        for (int i = 0; i < vertexCount; i++) {
            adjacencyMatrix.add(new ArrayList<>());
        }

        for (Edge edge : edges) {
            if (!vertices.contains(edge.getSource())) {
                vertices.add(edge.getSource());
            }
            if (!vertices.contains(edge.getTarget())) {
                vertices.add(edge.getTarget());
            }
            addEdge(vertices.indexOf(edge.getSource()), vertices.indexOf(edge.getTarget()));
        }
    }

    private void addEdge(int v, int w) {
        adjacencyMatrix.get(v).add(w);
        adjacencyMatrix.get(w).add(v);
    }

    private boolean checkForK5() {
        for (int x = 0; x < vertexCount; x++)
            for (int y : adjacencyMatrix.get(x))
                for (int z : adjacencyMatrix.get(y))
                    if (adjacencyMatrix.get(x).contains(z))
                        for (int w : adjacencyMatrix.get(z))
                            if (adjacencyMatrix.get(y).contains(w) && adjacencyMatrix.get(x).contains(w))
                                for (int u : adjacencyMatrix.get(w))
                                    if (adjacencyMatrix.get(x).contains(u) && adjacencyMatrix.get(y).contains(u) && adjacencyMatrix.get(z).contains(u)) {
                                        Set<Integer> vertexSet = new HashSet<>(Arrays.asList(x, y, z, w, u));
                                        if (vertexSet.size() == 5) return true;
                                    }
        return false;
    }

    private boolean checkForK33() {
        for (int x = 0; x < vertexCount; x++)
            for (int y : adjacencyMatrix.get(x))
                for (int z : adjacencyMatrix.get(y))
                    for (int w : adjacencyMatrix.get(z))
                        if (adjacencyMatrix.get(x).contains(w))
                            for (int u : adjacencyMatrix.get(w))
                                if (adjacencyMatrix.get(y).contains(u))
                                    for (int v : adjacencyMatrix.get(u))
                                        if (adjacencyMatrix.get(x).contains(v) && adjacencyMatrix.get(z).contains(v)) {
                                            Set<Integer> vertexSet = new HashSet<>(Arrays.asList(x, y, z, w, u, v));
                                            if (vertexSet.size() == 6) return true;
                                        }
        return false;
    }
}
