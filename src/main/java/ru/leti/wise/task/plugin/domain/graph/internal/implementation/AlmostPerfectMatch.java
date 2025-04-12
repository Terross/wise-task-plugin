package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class AlmostPerfectMatch implements GraphProperty {

    private static boolean depthFirstSearch(int v, Map<Integer, List<Integer>> adjList, Set<Integer> visited, Map<Integer, Integer> matching) {
        if (visited.contains(v)) {
            return false;
        }
        visited.add(v);
        for (int neighbor : adjList.get(v)) {
            if (!matching.containsKey(neighbor)) {
                matching.put(neighbor, v);
                return true;
            } else {
                if (depthFirstSearch(matching.get(neighbor), adjList, visited, matching)) {
                    matching.put(neighbor, v);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isBipartite(Graph graph) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> colors = new HashMap<>();
        Map<Integer, List<Integer>> adjList = new HashMap<>();

        for (Vertex vertex : graph.getVertexList()) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : graph.getEdgeList()) {
            adjList.get(edge.getSource()).add(edge.getTarget());
            adjList.get(edge.getTarget()).add(edge.getSource());
        }

        for (Vertex vertex : graph.getVertexList()) {
            int vertexId = vertex.getId();
            if (!visited.contains(vertexId)) {
                colors.put(vertexId, 1);
                queue.add(vertexId);
                while (!queue.isEmpty()) {
                    int current = queue.poll();
                    visited.add(current);
                    for (int neighbor : adjList.get(current)) {
                        if (colors.containsKey(neighbor)) {
                            if (colors.get(neighbor).equals(colors.get(current))) {
                                return false;
                            }
                        } else {
                            colors.put(neighbor, 1 - colors.get(current));
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return true;
    }

    private static int findMaxMatchingSize(Graph graph) {
        Map<Integer, Integer> matching = new HashMap<>();
        Map<Integer, List<Integer>> adjList = new HashMap<>();

        for (Vertex vertex : graph.getVertexList()) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : graph.getEdgeList()) {
            adjList.get(edge.getSource()).add(edge.getTarget());
            adjList.get(edge.getTarget()).add(edge.getSource());
        }

        int answer = 0;
        for (Vertex vertex : graph.getVertexList()) {
            int vertexId = vertex.getId();
            Set<Integer> visited = new HashSet<>();
            if (depthFirstSearch(vertexId, adjList, visited, matching)) {
                answer++;
            }
        }

        return answer;
    }

    @Override
    public boolean run(Graph graph) {
        int maxMatchingSize = findMaxMatchingSize(graph);
        int numVertices = graph.getVertexList().size();

        if (!isBipartite(graph)) {
            return false;
        }

        return numVertices % 2 != 0 && maxMatchingSize == numVertices - 1;
    }
}
