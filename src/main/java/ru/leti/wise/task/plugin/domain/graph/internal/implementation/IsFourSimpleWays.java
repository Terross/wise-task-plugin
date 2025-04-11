package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsFourSimpleWays implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        // check if all vertices have degree of at least 4
        if (!allVerticesHaveDegreeOfAtLeastLimit(graph, 4)) {
            return false;
        }

        // choose an initial vertex from the graph's vertices
        Vertex initialVertex = graph.getVertexList().get(0);

        // iterate through all vertices in the graph
        for (Vertex currentVertex : graph.getVertexList()) {
            // check if there are at least four simple paths between the initial vertex and the current vertex
            if (!hasFourSimplePathsBetween(initialVertex, currentVertex, graph)) {
                return false;
            }
        }

        return true;
    }

    public static boolean allVerticesHaveDegreeOfAtLeastLimit(Graph graph, Integer limit) {
        Map<Integer, Integer> degrees = calculateDegrees(graph);

        for (Map.Entry<Integer, Integer> entry : degrees.entrySet()) {
            if (entry.getValue() < limit) {
                return false;
            }
        }

        return true;
    }

    public static Map<Integer, Integer> calculateDegrees(Graph graph) {
        Map<Integer, Integer> degrees = new HashMap<>();

        // initialize all vertex degrees to 0
        for (Vertex vertex : graph.getVertexList()) {
            degrees.put(vertex.getId(), 0);
        }

        // iterate through each edge in the graph
        for (Edge edge : graph.getEdgeList()) {
            int sourceVertexId = edge.getSource();
            int targetVertexId = edge.getTarget();

            // increment degrees for source
            degrees.put(sourceVertexId, degrees.get(sourceVertexId) + 1);

            // increment degrees for target as well in case of undirected graph
            if (!graph.isDirect()) {
                degrees.put(targetVertexId, degrees.get(targetVertexId) + 1);
            }
        }

        return degrees;
    }

    public List<Integer> getSimplePathBetween(Vertex start, Vertex end, Map<Integer, List<Integer>> adjacentList) {
        List<Integer> path = new ArrayList<>();
        Map<Integer, Integer> parentMap = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(start.getId());
        visited.add(start.getId());

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == end.getId()) {
                // path found, reconstruct it
                while (current != -1) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path); // reverse path to get it in the correct order
                return path;
            }

            for (int neighbor : adjacentList.get(current)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        return path;
    }

    public void removePathFromAdjacentList(List<Integer> path, Map<Integer, List<Integer>> adjacentList, Graph graph) {
        if (path.isEmpty()) { return; }

        for (int i = 0; i < path.size() - 1; i++) {
            int current = path.get(i);
            int next = path.get(i + 1);

            // remove the edge from current to next in the adjacent list
            adjacentList.get(current).remove((Integer) next);

            // if the graph is undirected, also remove the edge from next to current
            if (!graph.isDirect()) {
                adjacentList.get(next).remove((Integer) current);
            }
        }
    }

    public boolean hasFourSimplePathsBetween(Vertex first, Vertex second, Graph graph) {
        // for simplicity call hasFourSimplePathsBetween for same vertex is true
        if (first.equals(second)) {
            return true;
        }

        // create adjacent list
        Map<Integer, List<Integer>> adjacentList = getAdjacentList(graph);
        int totalPaths = 0;

        do {
            // try to find simple path
            List<Integer> path = getSimplePathBetween(first, second, adjacentList);

            if (path.isEmpty()) {
                return false;
            }

            removePathFromAdjacentList(path, adjacentList, graph);
            totalPaths++;
        } while (totalPaths < 4);

        return true;
    }

    public Map<Integer, List<Integer>> getAdjacentList(Graph graph) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

        // iterate through all edges in the graph
        for (Edge edge : graph.getEdgeList()) {
            int sourceVertexId = edge.getSource();
            int targetVertexId = edge.getTarget();

            // initialize the list for the source vertex if it doesn't exist and add the target vertex to the source vertex's list
            adjacencyList.putIfAbsent(sourceVertexId, new ArrayList<>());
            adjacencyList.get(sourceVertexId).add(targetVertexId);

            // if the graph is undirected, also add the source vertex to the target vertex's list
            if (!graph.isDirect()) {
                adjacencyList.putIfAbsent(targetVertexId, new ArrayList<>());
                adjacencyList.get(targetVertexId).add(sourceVertexId);
            }
        }

        return adjacencyList;
    }
}
