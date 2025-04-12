package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class FourConnectedGraph implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        if (graph.getVertexList().size() < 5) {
            return false;
        }

        List<Vertex> vertices = graph.getVertexList();
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                for (int k = j + 1; k < vertices.size(); k++) {
                    Graph copy = createGraphCopyWithoutVertices(graph, vertices.get(i).getId(), vertices.get(j).getId(), vertices.get(k).getId());
                    if (!isGraphConnected(copy)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean isGraphConnected(Graph graph) {
        if (graph.getVertexList().isEmpty()) {
            return true;
        }

        Set<Integer> visited = new HashSet<>();
        int startVertexId = graph.getVertexList().get(0).getId();
        dfs(graph, startVertexId, visited);

        return visited.size() == graph.getVertexList().size();
    }

    private static void dfs(Graph graph, int currentVertexId, Set<Integer> visited) {
        visited.add(currentVertexId);
        for (Edge edge : graph.getEdgeList()) {
            int nextVertexId = edge.getSource() == currentVertexId ? edge.getTarget() : (edge.getTarget() == currentVertexId ? edge.getSource() : -1);
            if (nextVertexId != -1 && !visited.contains(nextVertexId)) {
                dfs(graph, nextVertexId, visited);
            }
        }
    }

    private static Graph createGraphCopyWithoutVertices(Graph original, int... verticesToRemove) {
        Set<Integer> verticesToExclude = new HashSet<>();
        for (int v : verticesToRemove) {
            verticesToExclude.add(v);
        }

        List<Vertex> newVertices = new ArrayList<>();
        List<Edge> newEdges = new ArrayList<>();

        for (Vertex vertex : original.getVertexList()) {
            if (!verticesToExclude.contains(vertex.getId())) {
                newVertices.add(vertex);
            }
        }

        for (Edge edge : original.getEdgeList()) {
            if (!verticesToExclude.contains(edge.getSource()) && !verticesToExclude.contains(edge.getTarget())) {
                newEdges.add(edge);
            }
        }

        return Graph.builder()
                .vertexList(newVertices)
                .edgeList(newEdges)
                .vertexCount(newVertices.size())
                .edgeCount(newEdges.size())
                .isDirect(original.isDirect())
                .build();
    }
}
