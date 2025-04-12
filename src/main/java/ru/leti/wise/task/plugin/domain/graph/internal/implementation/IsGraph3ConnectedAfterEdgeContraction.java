package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsGraph3ConnectedAfterEdgeContraction implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        if (graph.getVertexCount() < 3) return false;
        else if (graph.getVertexCount() == 3) {
            return true;
        }
        if (is3Connected(graph)) {
            for (Edge e : graph.getEdgeList()) {
                if (is3Connected(edgeContraction(graph, e))) {
                    return true;
                }
            }
        }
        return false;
    }

    private Graph edgeContraction(Graph graph, Edge edge) {
        int curVertex = edge.getSource();
        int exVertex = edge.getTarget();
        List<Edge> newEdges = new ArrayList<>();

        // Переносим рёбра в новый список с учётом сжатия рёбер
        for (Edge e : graph.getEdgeList()) {
            if (e.getSource() == exVertex && e.getTarget() != curVertex) {
                newEdges.add(new Edge(curVertex, e.getTarget(), e.getColor(), e.getWeight(), e.getLabel()));
            } else if (e.getTarget() == exVertex && e.getSource() != curVertex) {
                newEdges.add(new Edge(e.getSource(), curVertex, e.getColor(), e.getWeight(), e.getLabel()));
            } else {
                newEdges.add(e);
            }
        }

        // Убираем удалённую вершину
        List<Vertex> newVertexList = new ArrayList<>();
        for (Vertex vertex : graph.getVertexList()) {
            if (vertex.getId() != exVertex) {
                newVertexList.add(vertex);
            }
        }

        return new Graph(graph.getVertexCount() - 1, newEdges.size(), graph.isDirect(), newEdges, newVertexList);
    }

    private boolean is3Connected(Graph graph) {
        for (Vertex v : graph.getVertexList()) {
            for (Vertex w : graph.getVertexList()) {
                if (!v.equals(w)) {
                    List<Edge> tmpEdges = new ArrayList<>(graph.getEdgeList());
                    List<Vertex> tmpVertices = new ArrayList<>(graph.getVertexList());

                    // Убираем вершины v и w и их рёбра
                    tmpEdges.removeIf(e -> e.getSource() == v.getId() || e.getTarget() == v.getId() ||
                                           e.getSource() == w.getId() || e.getTarget() == w.getId());
                    tmpVertices.removeIf(vertex -> vertex.getId() == v.getId() || vertex.getId() == w.getId());

                    Graph graph1 = new Graph(tmpVertices.size(), tmpEdges.size(), graph.isDirect(), tmpEdges, tmpVertices);

                    if (!isGraphConnected(graph1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isGraphConnected(Graph graph) {
        int startVertexId = graph.getVertexList().get(0).getId();
        Set<Integer> visited = DFS(graph, startVertexId);
        for (Vertex v : graph.getVertexList()) {
            if (!visited.contains(v.getId())) {
                return false;
            }
        }
        return true;
    }

    private void DFSUtil(Graph graph, int vertexId, Set<Integer> visitedSet) {
        visitedSet.add(vertexId);
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == vertexId && !visitedSet.contains(edge.getTarget())) {
                DFSUtil(graph, edge.getTarget(), visitedSet);
            }
            if (edge.getTarget() == vertexId && !visitedSet.contains(edge.getSource())) {
                DFSUtil(graph, edge.getSource(), visitedSet);
            }
        }
    }

    private Set<Integer> DFS(Graph graph, int startVertexId) {
        Set<Integer> visitedSet = new HashSet<>();
        DFSUtil(graph, startVertexId, visitedSet);
        return visitedSet;
    }
}
