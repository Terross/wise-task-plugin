package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class EdgeCriticalConnected implements GraphProperty {

     public static Set<Integer> depthFirstSearch(Graph graph, int startVertexId) {
        Set<Integer> visitedSet = new HashSet<>();
        depthFirstSearchUtil(graph, startVertexId, visitedSet);
        return visitedSet;
    }

    private static void depthFirstSearchUtil(Graph graph, int vertexId, Set<Integer> visitedSet) {
        visitedSet.add(vertexId);

        for (Edge edge : graph.getEdgeList()) {
            int targetId = (edge.getSource() == vertexId) ? edge.getTarget() : (edge.getTarget() == vertexId) ? edge.getSource() : -1;

            if (targetId != -1 && !visitedSet.contains(targetId)) {
                depthFirstSearchUtil(graph, targetId, visitedSet);
            }
        }
    }

    @Override
    public boolean run(Graph graph) {
        // Find the marked edge as the first blue edge
        Edge markedEdge = null;
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getColor().equals(Color.BLUE)) {
                markedEdge = edge;
                break;
            }
        }
        if (markedEdge == null) {
            return false;
        }

        // Find the edge-connectivity of the original graph
        Integer originalConnectivity = findKConnectivityOfGraph(graph);

        // Remove the marked edge from the graph
        List<Edge> edges = graph.getEdgeList();
        edges.remove(markedEdge);
        graph.setEdgeList(new ArrayList<>(edges));

        // Find the edge-connectivity of the new graph without the marked edge
        Integer newGraphConnectivity = findKConnectivityOfGraph(graph);

        // Restore the original graph
        graph.setEdgeList(edges);

        // Return true if the edge-connectivity of the graph changes after removing the marked edge
        return !newGraphConnectivity.equals(originalConnectivity);
    }

    public static List<List<Edge>> getSuperSetOfEdges(List<Edge> edges) {
        int n = edges.size();
        List<List<Edge>> subsets = new ArrayList<>();

        // Generate all possible subsets
        for (int i = 0; i < (1 << n); i++) {
            List<Edge> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    subset.add(edges.get(j));
                }
            }
            subsets.add(subset);
        }

        // Sort the subsets by size in descending order
        subsets.sort((a, b) -> b.size() - a.size());

        return subsets;
    }

    public static Integer findKConnectivityOfGraph(Graph graph) {
        List<Edge> edges = graph.getEdgeList();
        List<List<Edge>> subsets = getSuperSetOfEdges(edges);

        // Check if the graph is connected after removing each subset of edges
        for (List<Edge> subset : subsets) {
            // Remove the subset of edges from the graph
            graph.setEdgeList(subset);
            // Check if the graph is connected
            if (!isGraphConnected(graph)) {
                // Restore the original graph
                graph.setEdgeList(edges);
                // Return the edge-connectivity of the graph
                return edges.size() - subset.size();
            }
        }

        // Restore the original graph
        graph.setEdgeList(edges);

        return 0;
    }

    public static boolean isGraphConnected(Graph graph) {
        // Perform a depth-first search on the graph
        // starting from the first vertex
        int startVertexId = graph.getVertexList().get(0).getId();


        // Get the set of visited vertices
        Set<Integer> visited = depthFirstSearch(graph, startVertexId);

        // Check if all vertices have been visited
        for (Vertex v : graph.getVertexList()) {
            if (!visited.contains(v.getId())) {
                return false;
            }
        }

        return true;
    }
}
