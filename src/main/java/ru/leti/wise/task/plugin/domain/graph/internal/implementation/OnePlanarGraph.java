package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class OnePlanarGraph implements GraphProperty {

    private final Map<Integer, List<Integer>> adjacencyList;

    public OnePlanarGraph() {
        this.adjacencyList = new HashMap<>();
    }

    @Override
    public boolean run(Graph graph) {
        if (graph.getVertexCount() <= 4) {
            return true;
        }

        if (graph.getEdgeCount() > 4 * graph.getVertexCount() - 8) {
            return false;
        }

        buildAdjacencyList(graph);
        resetVertexWeights(graph);

        Queue<Integer> queue = new LinkedList<>();
        int chromaticNumber = 0;

        int startVertex = graph.getVertexList().get(0).getId();
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.poll();
            boolean[] colorsUsedByNeighbors = new boolean[chromaticNumber + 2];

            for (int neighbor : adjacencyList.get(currentVertex)) {
                int color = graph.getVertexList().get(neighbor).getWeight();
                if (color != 0) {
                    colorsUsedByNeighbors[color] = true;
                }
            }

            int color = 1;
            while (colorsUsedByNeighbors[color]) {
                color++;
            }

            graph.getVertexList().get(currentVertex).setWeight(color);
            if (color > chromaticNumber) {
                chromaticNumber = color;
            }

            for (int neighbor : adjacencyList.get(currentVertex)) {
                if (graph.getVertexList().get(neighbor).getWeight() == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return chromaticNumber <= 6;
    }

    private void buildAdjacencyList(Graph graph) {
        adjacencyList.clear();
        for (Vertex vertex : graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new ArrayList<>());
        }
        for (Edge edge : graph.getEdgeList()) {
            int from = edge.getSource();
            int to = edge.getTarget();
            adjacencyList.get(from).add(to);
            adjacencyList.get(to).add(from);
        }
    }

    private void resetVertexWeights(Graph graph) {
        for (Vertex vertex : graph.getVertexList()) {
            vertex.setWeight(0);
        }
    }
}
