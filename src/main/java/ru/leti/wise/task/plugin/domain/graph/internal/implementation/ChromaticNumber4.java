package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;


@Component
public class ChromaticNumber4 implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        if (graph.getVertexCount() < 4) {
            return false;
        }

        List<Edge> edges = graph.getEdgeList();
        List<Vertex> vertices = graph.getVertexList();
        List<Vertex> sortedVertices = sortVertices(vertices, edges);

        Map<Integer, Integer> colors = new HashMap<>();
        for (Vertex vertex : vertices) {
            colors.put(vertex.getId(), -1);
        }

        int chromaticNum = 0;
        while (!sortedVertices.isEmpty()) {
            chromaticNum += 1;
            if (chromaticNum > 4) {
                return false;
            }

            Vertex currentVertex = sortedVertices.remove(0);
            colors.put(currentVertex.getId(), chromaticNum);

            List<Vertex> toDelete = new ArrayList<>();
            for (Vertex vertex : sortedVertices) {
                if (!isAdjacent(vertex, currentVertex, edges) && colors.get(vertex.getId()) == -1) {
                    boolean canColor = true;
                    for (Edge edge : edges) {
                        if ((edge.getSource() == vertex.getId() && colors.get(edge.getTarget()) == chromaticNum) ||
                                (edge.getTarget() == vertex.getId() && colors.get(edge.getSource()) == chromaticNum)) {
                            canColor = false;
                            break;
                        }
                    }
                    if (canColor) {
                        colors.put(vertex.getId(), chromaticNum);
                        toDelete.add(vertex);
                    }
                }
            }

            sortedVertices.removeAll(toDelete);
            sortedVertices = update(sortedVertices, edges);
        }

        return chromaticNum == 4;
    }

    private List<Vertex> sortVertices(List<Vertex> vertices, List<Edge> edges) {
        List<Vertex> sorted = new ArrayList<>(vertices);

        sorted.sort((v1, v2) -> {
            int degreeDiff = degree(v2, edges) - degree(v1, edges);
            if (degreeDiff == 0) {
                int v1Sum = 0;
                int v2Sum = 0;
                for (Edge edge : edges) {
                    if (edge.getSource() == v1.getId() || edge.getTarget() == v1.getId()) {
                        v1Sum += degree(getVertexById(vertices, edge.getSource() == v1.getId() ? edge.getTarget() : edge.getSource()), edges);
                    }
                    if (edge.getSource() == v2.getId() || edge.getTarget() == v2.getId()) {
                        v2Sum += degree(getVertexById(vertices, edge.getSource() == v2.getId() ? edge.getTarget() : edge.getSource()), edges);
                    }
                }
                degreeDiff = v2Sum - v1Sum;
            }
            return Integer.compare(degreeDiff, 0);
        });

        return sorted;
    }

    private int degree(Vertex vertex, List<Edge> edges) {
        return (int) edges.stream()
                .filter(edge -> edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId())
                .count();
    }

    private boolean isAdjacent(Vertex v1, Vertex v2, List<Edge> edges) {
        return edges.stream().anyMatch(edge ->
                (edge.getSource() == v1.getId() && edge.getTarget() == v2.getId()) ||
                        (edge.getSource() == v2.getId() && edge.getTarget() == v1.getId())
        );
    }

    private List<Vertex> update(List<Vertex> vertices, List<Edge> edges) {
        List<Edge> filteredEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (vertices.stream().anyMatch(v -> v.getId() == edge.getSource()) &&
                    vertices.stream().anyMatch(v -> v.getId() == edge.getTarget())) {
                filteredEdges.add(edge);
            }
        }
        return sortVertices(vertices, filteredEdges);
    }

    private Vertex getVertexById(List<Vertex> vertices, int id) {
        return vertices.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
    }
}
