package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsGraphEdge3OddConnected implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        if (graph.getEdgeCount() == 0) return false;
        if (graph.getVertexCount() % 2 != 0) return false;
        return isEdge3Connected(graph);
    }

    public boolean isEdge3Connected(Graph graph) {
        for (Edge e1 : graph.getEdgeList()) {
            for (Edge e2 : graph.getEdgeList()) {
                List<Edge> tmpEdges = new ArrayList<>(graph.getEdgeList());
                tmpEdges.remove(e1);
                if (!e1.equals(e2)) {
                    tmpEdges.remove(e2);
                }
                Graph graph1 = new Graph(graph.getVertexCount(), tmpEdges.size(), graph.isDirect(), tmpEdges, graph.getVertexList());
                if (!isGraphConnected(graph1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGraphConnected(Graph graph) {
        int startVertexId = graph.getVertexList().get(0).getId();
        Set<Integer> visited = DFS(graph, startVertexId);
        for (Vertex v : graph.getVertexList()) {
            if (!visited.contains(v.getId())) {
                return false;
            }
        }
        return true;
    }

    private void DFSFunction(Graph graph, int vertexId, Set<Integer> visitedSet) {
        visitedSet.add(vertexId);
        for (Edge e : graph.getEdgeList()) {
            if (e.getSource() == vertexId && !visitedSet.contains(e.getTarget())) {
                DFSFunction(graph, e.getTarget(), visitedSet);
            }
            if (e.getTarget() == vertexId && !visitedSet.contains(e.getSource())) {
                DFSFunction(graph, e.getSource(), visitedSet);
            }
        }
    }

    private Set<Integer> DFS(Graph graph, int startVertexId) {
        Set<Integer> visitedSet = new HashSet<>();
        DFSFunction(graph, startVertexId, visitedSet);
        return visitedSet;
    }
}
