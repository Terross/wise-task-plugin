package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import java.util.*;

@Component
public class IsNormal implements GraphProperty {
    private int vertexCount;
    private boolean flag;
    private boolean[] isVisited;
    private List<List<Integer>> adjacencyList;
    private List<Edge> edges;
    private List<Vertex> vertices;

    @Override
    public boolean run(Graph graph) {
        setValues(graph);
        for (int i = 0; i < vertexCount; i++) {
            if (!flag) break;
            dfs(i);
        }
        return flag;
    }

    private void setValues(Graph graph) {
        flag = true;
        vertexCount = graph.getVertexCount();
        edges = graph.getEdgeList();
        vertices = graph.getVertexList();
        adjacencyList = new ArrayList<>(vertexCount);
        isVisited = new boolean[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (Edge edge : edges) {
            addEdge(edge.getSource(), edge.getTarget());
        }
    }

    private void addEdge(int v, int w) {
        adjacencyList.get(v).add(w);
        adjacencyList.get(w).add(v);
    }

    private void dfs(int current) {
        isVisited[current] = true;
        for (int target : adjacencyList.get(current)) {
            Color currentColor = vertices.get(current).getColor();
            Color targetColor = vertices.get(target).getColor();
            if (currentColor == targetColor && currentColor != Color.GRAY) {
                flag = false;
                return;
            }
            if (!isVisited[target] && flag) {
                dfs(target);
            }
        }
    }
}
