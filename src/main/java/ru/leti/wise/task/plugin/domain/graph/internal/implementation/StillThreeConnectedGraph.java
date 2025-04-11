package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class StillThreeConnectedGraph implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        List<Edge> edges = graph.getEdgeList();
        if (!ThreeConnected(graph)) {
            return false;
        } else {
            for (Edge edge : edges) {
                Graph graphtmp = removeEdge(graph, edge);
                if (ThreeConnected(graphtmp)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean ThreeConnected(Graph graph) {
        if (graph.getVertexCount() <= 3) {
            return false;
        }
        List<Vertex> vertices = new ArrayList<>(graph.getVertexList());
        for (Vertex vertex1 : vertices) {
            for (Vertex vertex2 : vertices) {
                if (vertex1.equals(vertex2)) {
                    continue;
                } else {
                    // Создаем копию графа перед удалением вершин и связанных с ними рёбер
                    Graph newGraph = removeVertexAndEdges(graph, vertex1);
                    newGraph = removeVertexAndEdges(newGraph, vertex2);

                    // Проверяем, остается ли граф связным после удаления двух вершин
                    if (!isConnected(newGraph)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public Graph removeEdge(Graph graph, Edge edgeToRemove) {
        // Создаем новый граф, который будет копией исходного
        Graph newGraph = Graph.builder()
                .vertexCount(graph.getVertexCount())
                .edgeCount(graph.getEdgeCount())
                .isDirect(graph.isDirect())
                .edgeList(new ArrayList<>(graph.getEdgeList()))
                .vertexList(new ArrayList<>(graph.getVertexList()))
                .build();

        // Удаляем указанное ребро из списка рёбер нового графа
        newGraph.getEdgeList().remove(edgeToRemove);

        return newGraph;
    }

    public Graph removeVertexAndEdges(Graph graph, Vertex vertexToRemove) {
        // Создаем копию графа
        Graph newGraph = Graph.builder()
                .vertexCount(graph.getVertexCount())
                .edgeCount(graph.getEdgeCount())
                .isDirect(graph.isDirect())
                .edgeList(new ArrayList<>(graph.getEdgeList()))
                .vertexList(new ArrayList<>(graph.getVertexList()))
                .build();

        // Удаляем связанные рёбра
        removeEdgesConnectedToVertex(newGraph, vertexToRemove);

        // Удаляем саму вершину
        newGraph.getVertexList().remove(vertexToRemove);

        return newGraph;
    }

    private void removeEdgesConnectedToVertex(Graph graph, Vertex vertexToRemove) {
        // Получаем список рёбер графа
        List<Edge> edges = graph.getEdgeList();

        // Используем removeIf для удаления рёбер, связанных с удаляемой вершиной
        edges.removeIf(edge -> edge.getSource() == vertexToRemove.getId() || edge.getTarget() == vertexToRemove.getId());
    }


    private boolean isConnected(Graph graph) {
        if (graph.getVertexList().isEmpty()) {
            return true;
        }

        Set<Integer> visited = new HashSet<>();
        Vertex startVertex = graph.getVertexList().get(0); // Начинаем с первой вершины
        dfs(graph, startVertex, visited);
        return visited.size() == graph.getVertexCount();
    }

    private void dfs(Graph graph, Vertex currentVertex, Set<Integer> visited) {
        visited.add(currentVertex.getId());
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == currentVertex.getId() || edge.getTarget() == currentVertex.getId()) {
                int nextVertexId = edge.getSource() == currentVertex.getId() ? edge.getTarget() : edge.getSource();
                Vertex nextVertex = graph.getVertexList().stream()
                        .filter(v -> v.getId() == nextVertexId)
                        .findFirst()
                        .orElse(null);
                if (nextVertex != null && !visited.contains(nextVertex.getId())) {
                    dfs(graph, nextVertex, visited);
                }
            }
        }
    }
}
