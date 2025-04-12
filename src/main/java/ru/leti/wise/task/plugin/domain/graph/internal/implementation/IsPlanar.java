package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import java.util.*;

@Component
public class IsPlanar implements GraphProperty {
    private int vertexCount;
    private List<List<Integer>> adjacencyList;
    private List<Edge> edges;
    private List<Vertex> vertices;

    @Override
    public boolean run(Graph graph) {
        setValues(graph);
        if (vertexCount <= 4) return true;
        return !(checkForK5() || checkForK33());
    }

    private void setValues(Graph graph) {
        vertexCount = graph.getVertexCount();
        edges = graph.getEdgeList();
        vertices = graph.getVertexList();
        adjacencyList = new ArrayList<>(vertexCount);

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

    private boolean checkForK5() {
        for (int x = 0; x < vertexCount; x++)
            for (int y : adjacencyList.get(x))
                for (int z : adjacencyList.get(y))
                    if (adjacencyList.get(x).contains(z))
                        for (int w : adjacencyList.get(z))
                            if (adjacencyList.get(y).contains(w) && adjacencyList.get(x).contains(w))
                                for (int u : adjacencyList.get(w))
                                    if (adjacencyList.get(x).contains(u) && adjacencyList.get(y).contains(u) && adjacencyList.get(z).contains(u)) {
                                        Set<Integer> vertexSet = new HashSet<>(Arrays.asList(x, y, z, w, u));
                                        if (vertexSet.size() == 5) return true;
                                    }
        return false;
    }

    private boolean checkForK33() {
        for (int x = 0; x < vertexCount; x++)
            for (int y : adjacencyList.get(x))
                for (int z : adjacencyList.get(y))
                    for (int w : adjacencyList.get(z))
                        if (adjacencyList.get(x).contains(w))
                            for (int u : adjacencyList.get(w))
                                if (adjacencyList.get(y).contains(u))
                                    for (int v : adjacencyList.get(u))
                                        if (adjacencyList.get(x).contains(v) && adjacencyList.get(z).contains(v)) {
                                            Set<Integer> vertexSet = new HashSet<>(Arrays.asList(x, y, z, w, u, v));
                                            if (vertexSet.size() == 6) return true;
                                        }
        return false;
    }
}
