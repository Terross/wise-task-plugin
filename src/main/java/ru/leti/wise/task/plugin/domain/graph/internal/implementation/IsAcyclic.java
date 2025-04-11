package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsAcyclic implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        int vertexCount = graph.getVertexCount();
        boolean[] visited = new boolean[vertexCount];
        boolean[] recStack = new boolean[vertexCount];

        int[][] adjMatrix = graphToAdjacencyMatrix(graph);

        for (int i = 0; i < vertexCount; i++) {
            if (isCyclic(i, visited, recStack, adjMatrix)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCyclic(int v, boolean[] visited, boolean[] recStack, int[][] adjMatrix) {
        if (recStack[v]) {
            return true;
        }
        if (visited[v]) {
            return false;
        }
        visited[v] = true;
        recStack[v] = true;

        for (int i = 0; i < adjMatrix.length; i++) {
            if (adjMatrix[v][i] != 0 && isCyclic(i, visited, recStack, adjMatrix)) {
                return true;
            }
        }

        recStack[v] = false;
        return false;
    }

    private List<Integer> getListVertices(Graph graph) {
        List<Integer> vertices = new ArrayList<>();
        for (Vertex vertex : graph.getVertexList()) {
            vertices.add(vertex.getId());
        }
        return vertices;
    }

    private int[][] graphToAdjacencyMatrix(Graph graph) {
        int vertexCount = graph.getVertexCount();
        boolean type = graph.isDirect();
        List<Integer> vertices = getListVertices(graph);
        int[][] adjMatrix = new int[vertexCount][vertexCount];

        List<Edge> edges = graph.getEdgeList();
        for (Edge edge : edges) {
            int fromIndex = vertices.indexOf(edge.getSource());
            int toIndex = vertices.indexOf(edge.getTarget());
            adjMatrix[fromIndex][toIndex] = 1;
            if (!type) {
                adjMatrix[toIndex][fromIndex] = 1;
            }
        }
        return adjMatrix;
    }
}
