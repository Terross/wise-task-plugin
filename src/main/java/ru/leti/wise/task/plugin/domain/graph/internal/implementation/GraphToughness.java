package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

@Component
public class GraphToughness implements GraphCharacteristic {

    private Map<Integer, List<Integer>> adjList_;
    private Map<Integer, Boolean> visited_;

    @Override
    public int run(Graph graph) {
        adjList_ = createAdjList(graph);
        visited_ = new HashMap<>();
        for (var vertexId : adjList_.keySet()) {
            visited_.put(vertexId, false);
        }
        return computeGraphToughness(graph);
    }

    private Map<Integer, List<Integer>> createAdjList(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();
        Map<Integer, List<Integer>> adjList = new HashMap<>();

        for (Vertex vertex : vertices) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : edges) {
            adjList.get(edge.getSource()).add(edge.getTarget());
            adjList.get(edge.getTarget()).add(edge.getSource());
        }
        return adjList;
    }

    private void dfs(int node, Map<Integer, Boolean> visited) {
        visited.put(node, true);
        for (var neighbor : adjList_.get(node)) {
            if (!visited.get(neighbor)) {
                dfs(neighbor, visited);
            }
        }
    }

    private int findConnectedComponents(Map<Integer, List<Integer>> tempAdjList, Map<Integer, Boolean> visited) {
        int componentCount = 0;
        for (var vertexId : tempAdjList.keySet()) {
            if (!visited.get(vertexId)) {
                componentCount++;
                dfs(vertexId, visited);
            }
        }
        return componentCount;
    }

    private Integer computeGraphToughness(Graph graph) {
        int n = adjList_.size();
        int minRatio = Integer.MAX_VALUE;

        for (var vertexId : adjList_.keySet()) {
            Map<Integer, Boolean> tempVisited = new HashMap<>(visited_);
            tempVisited.put(vertexId, true);

            int numComponents = findConnectedComponents(adjList_, tempVisited);
            int currentRatio = (n - 1) / numComponents;
            minRatio = Math.min(minRatio, currentRatio);
        }
        return minRatio;
    }
}