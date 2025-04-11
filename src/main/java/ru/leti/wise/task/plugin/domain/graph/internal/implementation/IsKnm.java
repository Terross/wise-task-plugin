package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsKnm implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        Set<Integer> redVertices = new HashSet<>(),
                blueVertices = new HashSet<>();

        for (Vertex vertex : graph.getVertexList()) {
            if (vertex.getColor() == Color.RED) redVertices.add(vertex.getId());
            else if (vertex.getColor() == Color.BLUE) blueVertices.add(vertex.getId());
        }

        if (!checkNumberOfEdges(graph, redVertices.size(), blueVertices.size()))
            return false;

        Map<Integer, Set<Integer>> connectedVerticesMap = createConnectedVerticesMap(graph);
        boolean areRedVerticesConnected = areAllVerticesConnected(connectedVerticesMap, redVertices, blueVertices),
                areBlueVerticesConnected = areAllVerticesConnected(connectedVerticesMap, blueVertices, redVertices);

        return areRedVerticesConnected && areBlueVerticesConnected;
    }

    private boolean checkNumberOfEdges(Graph graph, int n, int m) {
        return graph.getEdgeCount() == m * n;
    }

    private Map<Integer, Set<Integer>> createConnectedVerticesMap(Graph graph) {
        Map<Integer, Set<Integer>> connectedVerticesMap = new HashMap<>();
        for (Edge edge : graph.getEdgeList()) {
            connectedVerticesMap.computeIfAbsent(edge.getTarget(), k -> new HashSet<>()).add(edge.getSource());
            connectedVerticesMap.computeIfAbsent(edge.getSource(), k -> new HashSet<>()).add(edge.getTarget());
        }
        return connectedVerticesMap;
    }

    private boolean areAllVerticesConnected(Map<Integer, Set<Integer>> connectedVerticesMap, Set<Integer> partToCheck, Set<Integer> partConnected) {
        if (partConnected.isEmpty())
            return false;
        for (Integer vertexId : partToCheck) {
            if (!Objects.equals(connectedVerticesMap.get(vertexId), partConnected))
                return false;
        }
        return true;
    }
}
