package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class CalculateEdgeConnectivity implements GraphCharacteristic {

    @Override
    public int run(Graph graph) {
        if (graph.getEdgeCount() == 0 || !isConnected(graph)) {
            return 0;
        }
        return edgeConnectivity(graph);
    }

    public int edgeConnectivity(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.getEdgeList());
        int n = edges.size();

        if (n == 0) {
            return Integer.MAX_VALUE;
        }

        for (int k = 1; k <= n; k++) {
            List<List<Edge>> combinations = generateCombinations(edges, k);
            for (List<Edge> combination : combinations) {
                Graph newGraph = removeEdges(graph, combination);
                if (!isConnected(newGraph)) {
                    return k;
                }
            }
        }

        return n;
    }

    private List<List<Edge>> generateCombinations(List<Edge> edges, int k) {
        List<List<Edge>> combinations = new ArrayList<>();
        generateCombinationsHelper(edges, k, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private void generateCombinationsHelper(List<Edge> edges, int k, int start, List<Edge> currentCombination, List<List<Edge>> combinations) {
        if (currentCombination.size() == k) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        for (int i = start; i < edges.size(); i++) {
            currentCombination.add(edges.get(i));
            generateCombinationsHelper(edges, k, i + 1, currentCombination, combinations);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

    private Graph removeEdges(Graph graph, List<Edge> edgesToRemove) {
        List<Edge> newEdges = new ArrayList<>(graph.getEdgeList());
        newEdges.removeAll(edgesToRemove);

        return Graph.builder()
                .vertexCount(graph.getVertexCount())
                .edgeCount(newEdges.size())
                .isDirect(graph.isDirect())
                .vertexList(new ArrayList<>(graph.getVertexList()))
                .edgeList(newEdges)
                .build();
    }

    private boolean isConnected(Graph graph) {
        if (graph.getVertexList().isEmpty()) {
            return true;
        }

        Set<Integer> visited = new HashSet<>();
        int startVertexId = graph.getVertexList().get(0).getId();
        dfs(graph, startVertexId, visited);

        return visited.size() == graph.getVertexCount();
    }

    private void dfs(Graph graph, int currentVertexId, Set<Integer> visited) {
        visited.add(currentVertexId);
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == currentVertexId || edge.getTarget() == currentVertexId) {
                int nextVertexId = (edge.getSource() == currentVertexId) ? edge.getTarget() : edge.getSource();
                if (!visited.contains(nextVertexId)) {
                    dfs(graph, nextVertexId, visited);
                }
            }
        }
    }
}
