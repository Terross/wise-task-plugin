package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

@Component
public class CountDeficit implements GraphCharacteristic {
    @Override
    public int run(Graph graph) {
        if (!isBipartite(graph)) {
            return -1;
        }
        List<Vertex> vertices = graph.getVertexList();
        List<Edge> edges = graph.getEdgeList();

        int maxMatchingSize = findMaxMatchingSize(vertices, edges);
        int numOfVertices = vertices.size();
        int deficit = numOfVertices - maxMatchingSize;

        return deficit;
    }

    private static boolean isBipartite(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> colors = new HashMap<>();

        for (Vertex vertex : vertices) {
            if (!visited.contains(vertex.getId())) {
                Queue<Integer> queue = new LinkedList<>();
                queue.add(vertex.getId());
                colors.put(vertex.getId(), 0);

                while (!queue.isEmpty()) {
                    int currentVertex = queue.poll();
                    visited.add(currentVertex);

                    int currentColor = colors.get(currentVertex);
                    int neighborColor = 1 - currentColor;

                    for (Edge edge : graph.getEdgeList()) {
                        if (edge.getSource() == currentVertex) {
                            int neighborId = edge.getTarget();
                            if (!colors.containsKey(neighborId)) {
                                colors.put(neighborId, neighborColor);
                                queue.add(neighborId);
                            } else if (colors.get(neighborId) != neighborColor) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private static int findMaxMatchingSize(List<Vertex> vertices, List<Edge> edges) {
        Map<Integer, Integer> matching = new HashMap<>();
        boolean[] used = new boolean[vertices.size()];
        Map<Integer, Integer> vertexIndices = new HashMap<>();

        int index = 0;
        for (Vertex vertex : vertices) {
            vertexIndices.put(vertex.getId(), index++);
        }

        for (Vertex vertex : vertices) {
            matching.put(vertex.getId(), null);
        }

        int matchingSize = 0;
        for (Vertex vertex : vertices) {
            Arrays.fill(used, false);
            if (dfs(vertex.getId(), edges, matching, used, vertexIndices)) {
                matchingSize++;
            }
        }

        return matchingSize * 2;
    }

    private static boolean dfs(int v, List<Edge> edges, Map<Integer, Integer> matching, boolean[] used, Map<Integer, Integer> vertexIndices) {
        int index = vertexIndices.get(v);
        if (used[index]) return false;
        used[index] = true;

        for (Edge edge : edges) {
            if (edge.getSource() == v) {
                int to = edge.getTarget();
                if (matching.get(to) == null || dfs(matching.get(to), edges, matching, used, vertexIndices)) {
                    matching.put(to, v);
                    return true;
                }
            }
        }
        return false;
    }
}