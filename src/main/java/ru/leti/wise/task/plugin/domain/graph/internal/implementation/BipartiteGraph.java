package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class BipartiteGraph implements GraphProperty {

    private Map<Integer, List<Integer>> adjList_;

    @Override
    public boolean run(Graph graph) {
        adjList_ = createAdjList(graph);
        Map<Integer, Integer> color = new HashMap<>(graph.getVertexCount());

        for (var vertex : graph.getVertexList()) {
            color.put(vertex.getId(), -1);
        }

        for (var vertex : graph.getVertexList()) {
            if (color.get(vertex.getId()) == -1 && dfs(vertex.getId(), color, 0)) {
                return false;
            }
        }

        return true;
    }

    // Создание списка смежности для заданного графа
    private Map<Integer, List<Integer>> createAdjList(Graph graph) {
        Map<Integer, List<Integer>> adjList = new HashMap<>();

        for (Vertex vertex : graph.getVertexList()) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : graph.getEdgeList()) {
            adjList.get(edge.getSource()).add(edge.getTarget());
            adjList.get(edge.getTarget()).add(edge.getSource());
        }

        return adjList;
    }

    private boolean dfs(int src, Map<Integer, Integer> color, int c) {
        color.put(src, c);

        for (int neighbor : adjList_.get(src)) {
            if (color.get(neighbor) == -1) {
                if (dfs(neighbor, color, 1 - c)) {
                    return true;
                }
            } else if (Objects.equals(color.get(neighbor), color.get(src))) {
                return true;
            }
        }

        return false;
    }
}