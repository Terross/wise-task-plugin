package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FourCriticalGraph implements GraphProperty {

    // Метод для сортировки вершин по их степени
    private static List<Integer> sortVertices(List<Integer> vertices, List<Edge> edges) {
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

    // Метод для получения степени вершины
    private static int getDegree(int id, List<Edge> edges) {
        return (int) edges.stream()
                .filter(edge -> edge.getSource() == id || edge.getTarget() == id)
                .count();
    }

    // Подсчет суммы степеней всех соседних вершин
    private static int countConnectedDegrees(int id, List<Edge> edges) {
        return edges.stream()
                .filter(edge -> edge.getSource() == id || edge.getTarget() == id)
                .mapToInt(edge -> getDegree(edge.getSource() == id ? edge.getTarget() : edge.getSource(), edges))
                .sum();
    }

    // Метод для обновления списка ребер и сортировки вершин
    private List<Integer> update(List<Integer> vertices, List<Edge> edges) {
        List<Edge> filteredEdges = edges.stream()
                .filter(edge -> vertices.contains(edge.getSource()) && vertices.contains(edge.getTarget()))
                .collect(Collectors.toList());

        edges.clear();
        edges.addAll(filteredEdges);
        return sortVertices(vertices, filteredEdges);
    }

    private int getChromaticNumber(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.getEdgeList());
        List<Integer> sortedVertices = sortVertices(
                graph.getVertexList().stream().map(Vertex::getId).collect(Collectors.toList()), edges
        );

        Map<Integer, Integer> colors = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
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
                        .anyMatch(edge -> (edge.getSource() == v && edge.getTarget() == currentV) ||
                                (edge.getTarget() == v && edge.getSource() == currentV));

                if (!isAdjacent && colors.get(v) == -1) {
                    int finalChromaticNum = chromaticNum;
                    boolean canFill = edges.stream()
                            .noneMatch(edge -> (edge.getSource() == v && colors.get(edge.getTarget()) == finalChromaticNum) ||
                                    (edge.getTarget() == v && colors.get(edge.getSource()) == finalChromaticNum));

                    if (canFill) {
                        colors.put(v, chromaticNum);
                        toDelete.add(v);
                    }
                }
            }
            sortedVertices.removeAll(toDelete);
            sortedVertices = update(sortedVertices, edges);
        }
        return chromaticNum;
    }

    private Graph removeEdge(Graph graph, Edge edgeToRemove) {
        return Graph.builder()
                .vertexCount(graph.getVertexCount())
                .edgeCount(graph.getEdgeCount() - 1)
                .isDirect(graph.isDirect())
                .vertexList(new ArrayList<>(graph.getVertexList()))
                .edgeList(graph.getEdgeList().stream()
                        .filter(e -> !e.equals(edgeToRemove))
                        .collect(Collectors.toList()))
                .build();
    }

    private Graph removeVertexAndEdges(Graph graph, Vertex vertexToRemove) {
        List<Edge> updatedEdges = graph.getEdgeList().stream()
                .filter(edge -> edge.getSource() != vertexToRemove.getId() && edge.getTarget() != vertexToRemove.getId())
                .collect(Collectors.toList());

        List<Vertex> updatedVertices = graph.getVertexList().stream()
                .filter(v -> v.getId() != vertexToRemove.getId())
                .collect(Collectors.toList());

        return Graph.builder()
                .vertexCount(graph.getVertexCount() - 1)
                .edgeCount(updatedEdges.size())
                .isDirect(graph.isDirect())
                .vertexList(updatedVertices)
                .edgeList(updatedEdges)
                .build();
    }

    @Override
    public boolean run(Graph graph) {
        if (getChromaticNumber(graph) != 4) {
            return false;
        }

        for (Vertex vertex : graph.getVertexList()) {
            Graph newGraph = removeVertexAndEdges(graph, vertex);
            if (getChromaticNumber(newGraph) < 4) {
                return true;
            }
        }

        for (Edge edge : graph.getEdgeList()) {
            Graph newGraph = removeEdge(graph, edge);
            if (getChromaticNumber(newGraph) < 4) {
                return true;
            }
        }

        return false;
    }
}
