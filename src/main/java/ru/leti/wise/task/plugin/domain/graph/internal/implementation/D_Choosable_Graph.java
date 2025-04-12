package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;


import java.util.*;

@Component
public class D_Choosable_Graph implements GraphProperty {

    private Map<Integer, List<Integer>> buildAdjacencyList(Graph graph) {
        Map<Integer, List<Integer>> adjacencyList = new TreeMap<>();

        for (Vertex vertex : graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : graph.getEdgeList()) {
            adjacencyList.get(edge.getSource()).add(edge.getTarget());
            adjacencyList.get(edge.getTarget()).add(edge.getSource());
        }

        return adjacencyList;
    }

    private void resetWeights(Graph graph) {
        for (Vertex vertex : graph.getVertexList()) {
            vertex.setWeight(0);
        }
    }

    @Override
    public boolean run(Graph graph) {
        resetWeights(graph);
        Stack<Integer> stack = new Stack<>();
        List<Integer> visited = new ArrayList<>();
        Set<Integer> weightSet = new HashSet<>();
        int maxWeight = 1;
        int labelCounter = 0;

        Map<Integer, List<Integer>> adjacencyList = buildAdjacencyList(graph);
        stack.push(adjacencyList.keySet().iterator().next());

        while (!stack.isEmpty()) {
            int currentVertexId = stack.pop();
            visited.add(currentVertexId);

            Vertex currentVertex = graph.getVertexList().stream()
                    .filter(v -> v.getId() == currentVertexId)
                    .findFirst()
                    .orElseThrow();

            currentVertex.setWeight(1);
            currentVertex.setLabel(String.valueOf(labelCounter++));

            weightSet.clear();
            weightSet.add(0);

            for (int neighborId : adjacencyList.get(currentVertexId)) {
                Vertex neighbor = graph.getVertexList().stream()
                        .filter(v -> v.getId() == neighborId)
                        .findFirst()
                        .orElseThrow();

                weightSet.add(neighbor.getWeight());
            }

            for (int x = 0; x <= weightSet.size(); x++) {
                if (!weightSet.contains(x)) {
                    currentVertex.setWeight(x);
                    maxWeight = Math.max(maxWeight, x);
                    break;
                }
            }

            for (int neighborId : adjacencyList.get(currentVertexId)) {
                if (!visited.contains(neighborId) && !stack.contains(neighborId)) {
                    stack.push(neighborId);
                }
            }
        }

        return maxWeight <= 4;
    }
}
