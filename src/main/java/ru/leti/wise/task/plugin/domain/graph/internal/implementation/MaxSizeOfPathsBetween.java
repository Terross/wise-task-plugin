package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

@Component
public class MaxSizeOfPathsBetween implements GraphCharacteristic {
    private Map<Integer, List<Integer>> createAdjList(Graph graph) {
        Map<Integer, List<Integer>> adjList = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }
        for (Edge edge : graph.getEdgeList()) {
            adjList.get(edge.getSource()).add(edge.getTarget());
        }
        return adjList;
    }

    private int maxFlow(Graph graph, int sourceId, int sinkId) {
        int maxFlow = 0;
        int pathFlow;
        Map<Integer, List<Integer>> adjList = createAdjList(graph);
        List<Edge> edges = graph.getEdgeList();

        while ((pathFlow = augmentingPath(graph, adjList, sourceId, sinkId, edges)) > 0) {
            maxFlow += pathFlow;
        }
        return maxFlow;
    }

    private Edge getEdge(int current, int neighbour, List<Edge> edges) {
        for (Edge edge : edges) {
            if (current == edge.getSource() && neighbour == edge.getTarget()) {
                return edge;
            }
        }
        return null;
    }

    private int augmentingPath(Graph graph, Map<Integer, List<Integer>> adjList, int sourceId, int sinkId, List<Edge> edges) {
        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> parentMap = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(sourceId);
        visited.add(sourceId);
        boolean foundAugmentingPath = false;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            visited.add(current);
            for (int neighbour : adjList.get(current)) {
                Edge currEdge = getEdge(current, neighbour, edges);
                if (currEdge != null && !visited.contains(neighbour) && currEdge.getWeight() > 0) {
                    queue.add(neighbour);
                    parentMap.put(neighbour, current);
                    if (neighbour == sinkId) {
                        foundAugmentingPath = true;
                        break;
                    }
                }
            }
            if (foundAugmentingPath) break;
        }

        if (foundAugmentingPath) {
            int current = sinkId;
            int pathFlow = Integer.MAX_VALUE;
            while (current != sourceId) {
                int parent = parentMap.get(current);
                Edge edge = getEdge(parent, current, edges);
                if (edge != null) {
                    pathFlow = Math.min(pathFlow, edge.getWeight());
                }
                current = parent;
            }

            current = sinkId;
            while (current != sourceId) {
                int parent = parentMap.get(current);
                Edge edge = getEdge(parent, current, edges);
                if (edge != null) {
                    edge.setWeight(edge.getWeight() - pathFlow);
                }
                current = parent;
            }
            return pathFlow;
        }
        return 0;
    }

    @Override
    public int run(Graph graph) {
        Vertex source = null;
        Vertex sink = null;

        for (Vertex vertex : graph.getVertexList()) {
            if (vertex.getColor() == Color.GREEN) {
                source = vertex;
            } else if (vertex.getColor() == Color.RED) {
                sink = vertex;
            }
        }

        if (source == null || sink == null) {
            return -1;
        }

        for (Edge edge : graph.getEdgeList()) {
            edge.setWeight(1);
        }
        return maxFlow(graph, source.getId(), sink.getId());
    }
}
