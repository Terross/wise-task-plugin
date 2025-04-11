package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class ThreeCritical implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        return isThreeCritical(graph);
    }

    private boolean isThreeCritical(Graph graph) {
        List<Vertex> vertices = new ArrayList<>(graph.getVertexList());
        List<Edge> edges = graph.getEdgeList();

        // Проходим по всем вершинам
        for (Vertex vertex : vertices) {
            if (isThreeChromatic(graph)) {
                Graph newGraph = removeVertexAndEdges(graph, vertex);
                boolean checkLessChromatic = isLessThreeChromatic(newGraph);
                if (checkLessChromatic) {
                    return true;
                }
            } else {
                return false;
            }
        }

        // Проходим по всем рёбрам
        for (Edge edge : edges) {
            if (isThreeChromatic(graph)) {
                Graph newGraph = removeEdge(graph, edge);
                boolean checkLessChromatic = isLessThreeChromatic(newGraph);
                if (checkLessChromatic) {
                    return true;
                }
            } else {
                return false;
            }
        }

        return false;
    }

    private Graph removeEdge(Graph graph, Edge edgeToRemove) {
        List<Edge> newEdges = new ArrayList<>(graph.getEdgeList());
        newEdges.remove(edgeToRemove);

        return Graph.builder()
                .vertexCount(graph.getVertexCount())
                .edgeCount(newEdges.size())
                .isDirect(graph.isDirect())
                .edgeList(newEdges)
                .vertexList(graph.getVertexList())
                .build();
    }

    private boolean isThreeChromatic(Graph graph) {
        if (graph.getVertexCount() < 3) return false;
        List<Edge> edges = graph.getEdgeList();
        List<Vertex> vertices = graph.getVertexList();

        List<Vertex> sortedVertices = sortVertices(vertices, edges);

        Map<Integer, Integer> colors = new HashMap<>();
        for (Vertex vertex : vertices) {
            colors.put(vertex.getId(), -1);
        }

        int chromaticNum = 0;
        while (!sortedVertices.isEmpty()) {
            chromaticNum++;
            if (chromaticNum > 3) return false;

            Vertex currentV = sortedVertices.remove(0);
            colors.put(currentV.getId(), chromaticNum);
            List<Vertex> toDelete = new ArrayList<>();

            // Ищем не смежные вершины
            for (Vertex vertex : sortedVertices) {
                boolean isAdjacent = false;
                for (Edge edge : edges) {
                    if (edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId()) {
                        if (edge.getSource() == currentV.getId() || edge.getTarget() == currentV.getId()) {
                            isAdjacent = true;
                            break;
                        }
                    }
                }

                if (!isAdjacent && colors.get(vertex.getId()) == -1) {
                    boolean canFill = true;
                    for (Edge edge : edges) {
                        if (edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId()) {
                            if (edge.getSource() == vertex.getId() && colors.get(edge.getTarget()) == chromaticNum ||
                                    edge.getTarget() == vertex.getId() && colors.get(edge.getSource()) == chromaticNum) {
                                canFill = false;
                                break;
                            }
                        }
                    }

                    if (canFill) {
                        colors.put(vertex.getId(), chromaticNum);
                        toDelete.add(vertex);
                    }
                }
            }

            for (Vertex vertex : toDelete) {
                sortedVertices.remove(vertex);
            }

            sortedVertices = update(sortedVertices, edges);
        }
        return chromaticNum == 3;
    }

    private boolean isLessThreeChromatic(Graph graph) {
        if (graph.getVertexCount() < 3) return false;
        List<Edge> edges = graph.getEdgeList();
        List<Vertex> vertices = graph.getVertexList();

        List<Vertex> sortedVertices = sortVertices(vertices, edges);

        Map<Integer, Integer> colors = new HashMap<>();
        for (Vertex vertex : vertices) {
            colors.put(vertex.getId(), -1);
        }

        int chromaticNum = 0;
        while (!sortedVertices.isEmpty()) {
            chromaticNum++;
            if (chromaticNum > 3) return false;

            Vertex currentV = sortedVertices.remove(0);
            colors.put(currentV.getId(), chromaticNum);
            List<Vertex> toDelete = new ArrayList<>();

            for (Vertex vertex : sortedVertices) {
                boolean isAdjacent = false;
                for (Edge edge : edges) {
                    if (edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId()) {
                        if (edge.getSource() == currentV.getId() || edge.getTarget() == currentV.getId()) {
                            isAdjacent = true;
                            break;
                        }
                    }
                }

                if (!isAdjacent && colors.get(vertex.getId()) == -1) {
                    boolean canFill = true;
                    for (Edge edge : edges) {
                        if (edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId()) {
                            if (edge.getSource() == vertex.getId() && colors.get(edge.getTarget()) == chromaticNum ||
                                    edge.getTarget() == vertex.getId() && colors.get(edge.getSource()) == chromaticNum) {
                                canFill = false;
                                break;
                            }
                        }
                    }

                    if (canFill) {
                        colors.put(vertex.getId(), chromaticNum);
                        toDelete.add(vertex);
                    }
                }
            }

            for (Vertex vertex : toDelete) {
                sortedVertices.remove(vertex);
            }

            sortedVertices = update(sortedVertices, edges);
        }

        return chromaticNum < 3;
    }

    private List<Vertex> sortVertices(List<Vertex> vertices, List<Edge> edges) {
        vertices.sort((v1, v2) -> {
            int degreeDiff = degree(v2.getId(), edges) - degree(v1.getId(), edges);
            if (degreeDiff == 0) {
                int v1Count = getVertexWeightedDegree(v1, edges);
                int v2Count = getVertexWeightedDegree(v2, edges);
                degreeDiff = v2Count - v1Count;
            }
            return Integer.compare(degreeDiff, 0);
        });
        return vertices;
    }

    private int degree(int id, List<Edge> edges) {
        int degree = 0;
        for (Edge edge : edges) {
            if (edge.getSource() == id || edge.getTarget() == id) {
                degree++;
            }
        }
        return degree;
    }

    private int getVertexWeightedDegree(Vertex vertex, List<Edge> edges) {
        int degree = 0;
        for (Edge edge : edges) {
            if (edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId()) {
                degree += edge.getWeight() != null ? edge.getWeight() : 1; // assuming weight exists or default to 1
            }
        }
        return degree;
    }

    private List<Vertex> update(List<Vertex> vertices, List<Edge> edges) {
        List<Edge> filteredEdges = new ArrayList<>();
        for (Edge edge : edges) {

            Vertex v1 = Vertex.builder()
                    .id(edge.getSource())
                    .build();
            
            Vertex v2 = Vertex.builder()
                    .id(edge.getTarget())
                    .build();

            if (vertices.contains(v1) && vertices.contains(v2)) {
                filteredEdges.add(edge);
            }
        }
        return sortVertices(vertices, filteredEdges);
    }

    public static Graph removeVertexAndEdges(Graph graph, Vertex vertexToRemove) {
        List<Edge> newEdges = new ArrayList<>(graph.getEdgeList());
        newEdges.removeIf(edge -> edge.getSource() == vertexToRemove.getId() || edge.getTarget() == vertexToRemove.getId());

        List<Vertex> newVertexList = new ArrayList<>(graph.getVertexList());
        newVertexList.remove(vertexToRemove);

        return Graph.builder()
                .vertexCount(graph.getVertexCount() - 1)
                .edgeCount(newEdges.size())
                .isDirect(graph.isDirect())
                .edgeList(newEdges)
                .vertexList(newVertexList)
                .build();
    }
}
