package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class NumberOfCliques implements GraphCharacteristic {

    @Override
    public int run(Graph graph) {
        if (graph.getVertexList() == null || graph.getEdgeList() == null || graph.getVertexList().isEmpty()) {
            throw new IllegalArgumentException("Граф не содержит вершин или ребер");
        }
        if (graph.isDirect()) {
            throw new IllegalArgumentException("Граф является ориентированным");
        }
        return findCliques(graph);
    }

    private int findCliques(Graph graph) {
        if (graph.getVertexList().size() == 1)
            return 1;

        Set<List<Integer>> cliques = new HashSet<>();
        Map<Integer, List<Integer>> adjacencyList = buildAdjacencyList(graph.getEdgeList());
        List<List<Boolean>> adjacencyMatrix = buildAdjacencyMatrix(graph, adjacencyList);

        for (int vert : adjacencyList.keySet()) {
            if (adjacencyList.get(vert).size() <= 2)
                continue;
            int max_clique = adjacencyList.get(vert).size(); // без петель n-1
            if (max_clique == graph.getVertexCount() - 1) {
                max_clique -= 1;
            }
            int system = adjacencyList.get(vert).size(); // Система счисления для перевода
            for (int max_current_clique = 2; max_current_clique <= max_clique; max_current_clique++) {
                for (int variants = (int) Math.pow(system, max_current_clique - 1); variants <= (int) Math.pow(system, max_current_clique); variants++) {
                    List<Integer> chosenVerts = toNNotation(variants, system);
                    if (new HashSet<>(chosenVerts).size() == chosenVerts.size()) {
                        List<Integer> clique = new ArrayList<>();
                        for (int i : chosenVerts) {
                            clique.add(adjacencyList.get(vert).get(i));
                        }
                        clique.add(vert);
                        Collections.sort(clique);
                        if (isFullGraph(adjacencyMatrix, clique, graph))
                            cliques.add(clique);
                    }
                }
            }
        }
        int additional = 0;
        if (((graph.getVertexCount() * (graph.getVertexCount() - 1)) / 2) == graph.getEdgeCount()) {
            additional = 1;
        }
        return cliques.size() + graph.getVertexCount() + graph.getEdgeCount() + additional;
    }

    private List<Integer> toNNotation(int x, int n) {
        List<Integer> y = new ArrayList<>();
        while (x != 0) {
            y.add(x % n);
            x /= n;
        }
        Collections.reverse(y);
        return y;
    }

    private boolean isFullGraph(List<List<Boolean>> adjacencyMatrix, List<Integer> clique, Graph graph) {
        boolean check = true;
        List<Integer> vertices = new ArrayList<>();
        for (Vertex vertex : graph.getVertexList()) {
            vertices.add(vertex.getId());
        }

        for (int v1 : clique) {
            for (int v2 : clique) {
                if (v1 != v2)
                    check = check && adjacencyMatrix.get(vertices.indexOf(v1)).get(vertices.indexOf(v2));
            }
        }
        return check;
    }

    private static Map<Integer, List<Integer>> buildAdjacencyList(List<Edge> edges) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        for (Edge edge : edges) {
            adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge.getTarget());
            adjacencyList.computeIfAbsent(edge.getTarget(), k -> new ArrayList<>()).add(edge.getSource());
        }
        return adjacencyList;
    }

    private static List<List<Boolean>> buildAdjacencyMatrix(Graph graph, Map<Integer, List<Integer>> adjacencyList) {
        List<List<Boolean>> adjacencyMatrix = new ArrayList<>();
        for (Vertex v1 : graph.getVertexList()) {
            List<Boolean> row = new ArrayList<>();
            for (Vertex v2 : graph.getVertexList()) {
                row.add(adjacencyList.getOrDefault(v1.getId(), Collections.emptyList()).contains(v2.getId()));
            }
            adjacencyMatrix.add(row);
        }
        return adjacencyMatrix;
    }
}
