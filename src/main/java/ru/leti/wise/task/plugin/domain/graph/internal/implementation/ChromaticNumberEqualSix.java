package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class ChromaticNumberEqualSix implements GraphProperty {

    public static List<Integer> sortVertices(List<Integer> vertices, List<Edge> edges) {
        List<Integer> sorted = new ArrayList<>(vertices);
        sorted.sort((v1, v2) -> {
            int degreeDiff = getDegree(v2, edges) - getDegree(v1, edges);
            if (degreeDiff != 0) {
                return degreeDiff;
            }
            return countConnectedDegrees(v2, edges) - countConnectedDegrees(v1, edges);
        });
        return sorted;
    }

    private static int getDegree(int id, List<Edge> edges) {
        return (int) edges.stream()
                .filter(edge -> edge.getSource() == id || edge.getTarget() == id)
                .count();
    }

    private static int countConnectedDegrees(int id, List<Edge> edges) {
        return edges.stream()
                .filter(edge -> edge.getSource() == id || edge.getTarget() == id)
                .mapToInt(edge -> getDegree(edge.getSource() == id ? edge.getTarget() : edge.getSource(), edges))
                .sum();
    }

    public List<Integer> update(List<Integer> vertices, List<Edge> edges) {
        List<Edge> filteredEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (vertices.contains(edge.getSource()) && vertices.contains(edge.getTarget())) {
                filteredEdges.add(edge);
            }
        }
        edges.clear();
        edges.addAll(filteredEdges);
        return sortVertices(vertices, filteredEdges);
    }

    @Override
    public boolean run(Graph graph) {
        if (graph.getVertexCount() < 6) {
            return false;
        }

        List<Edge> edges = graph.getEdgeList();
        List<Vertex> vertices = graph.getVertexList();
        List<Integer> sortedVertices = sortVertices(vertices.stream().map(Vertex::getId).toList(), edges);

        Map<Integer, Integer> colors = new HashMap<>();
        for (Vertex vertex : vertices) {
            colors.put(vertex.getId(), -1);
        }

        int chromaticNum = 0;
        while (!sortedVertices.isEmpty()) {
            chromaticNum++;
            int currentV = sortedVertices.remove(0);
            colors.put(currentV, chromaticNum);

            List<Integer> toDelete = new ArrayList<>();
            for (int v : sortedVertices) {
                boolean isAdjacent = edges.stream()
                        .anyMatch(edge -> (edge.getSource() == v && edge.getTarget() == currentV)
                                || (edge.getTarget() == v && edge.getSource() == currentV));

                if (!isAdjacent && colors.get(v) == -1) {
                    int finalChromaticNum = chromaticNum;
                    boolean canFill = edges.stream()
                            .filter(edge -> edge.getSource() == v || edge.getTarget() == v)
                            .noneMatch(edge -> colors.get(edge.getSource() == v ? edge.getTarget() : edge.getSource()) == finalChromaticNum);

                    if (canFill) {
                        colors.put(v, chromaticNum);
                        toDelete.add(v);
                    }
                }
            }
            sortedVertices.removeAll(toDelete);
            sortedVertices = update(sortedVertices, edges);
        }

        System.out.println(chromaticNum);
        return chromaticNum == 6;
    }
}
