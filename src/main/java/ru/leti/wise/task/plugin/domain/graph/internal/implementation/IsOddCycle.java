package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsOddCycle implements GraphProperty {
    private final List<Edge> chosenEdges = new ArrayList<>();
    private final Map<Integer, Integer> verticesColor = new HashMap<>();
    private final Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

    // this function is used to get red edges
    // fills chosenEdges array
    private void getChosenEdges(Graph graph) {
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getColor() == Color.RED) {
                chosenEdges.add(edge);
            }
        }
    }

    // this is used to get adjacent vertex by given edge and vertex
    // for undirected case
    private Integer getUndirectedConnectedVertex(Integer vertexId, Edge edge) {
        if (edge == null) {
            return null;
        }

        if (edge.getTarget() == vertexId) {
            return edge.getSource();
        } else if (edge.getSource() == vertexId) {
            return edge.getTarget();
        }

        return null;
    }


    // get edge that connected to passed vertex
    // for undirected case
    private Edge getUndirectedIncidentEdge(Integer vertexId) {
        for (Edge edge : chosenEdges) {
            if (edge.getTarget() == vertexId || edge.getSource() == vertexId) {
                return edge;
            }
        }
        return null;
    }

    // get edge that connected to passed vertex
    // for directed case
    private Edge getDirectedIncidentEdge(Integer vertexId) {
        for (Edge edge : chosenEdges) {
            if (edge.getSource() == vertexId) {
                return edge;
            }
        }
        return null;
    }


    // generates adjacency list for both cases: UNDIRECTED and DIRECTED graph
    private void getAdjacencyList(boolean graphType, List<Edge> edges) {
        for (Edge edge : edges) {
            if (!graphType) {
                adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge.getTarget());
                adjacencyList.computeIfAbsent(edge.getTarget(), k -> new ArrayList<>()).add(edge.getSource());
            } else {
                adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge.getTarget());
            }
        }
    }

    // this function is used to check if graph contains only 1 component
    // depth in search graph
    private void dfs(Integer vertex, int color) {
        verticesColor.put(vertex, color);
        if (adjacencyList.containsKey(vertex)) {
            for (Integer neighbor : adjacencyList.get(vertex)) {
                if (verticesColor.get(neighbor) == 0) {
                    dfs(neighbor, color);
                }
            }
        }
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private boolean checkIfContainsOnlyOneComponent(boolean graphType, List<Edge> edges) {
        getAdjacencyList(graphType, edges);

        for (Edge edge : edges) {
            verticesColor.put(edge.getSource(), 0);
            verticesColor.put(edge.getTarget(), 0);
        }

        int color = 1;
        while (verticesColor.containsValue(0)) {
            dfs(getKeyByValue(verticesColor, 0), color);
            color++;
        }

        Set<Integer> colors = new HashSet<>(verticesColor.values());
        return colors.size() == 1;
    }

    @Override
    public boolean run(Graph graph) {
        chosenEdges.clear();
        adjacencyList.clear();
        verticesColor.clear();

        getChosenEdges(graph);
        int parity = 0;

        if (chosenEdges.isEmpty()) {
            return false;
        }

        // check if graph contains only 1 component
        if (!checkIfContainsOnlyOneComponent(graph.isDirect(), chosenEdges)) {
            return false;
        }

        // this algorithm faces directed and undirected graphs
        if (!graph.isDirect()) {

            while (!chosenEdges.isEmpty()) {
                Integer start = chosenEdges.get(0).getSource();
                Edge currentEdge = getUndirectedIncidentEdge(start);

                if (currentEdge == null) {
                    return false;
                }

                Integer current = getUndirectedConnectedVertex(start, currentEdge);
                chosenEdges.remove(currentEdge);
                parity++;

                // this loop is used in case when we suddenly came to start,
                // but not all edges were used
                do {
                    currentEdge = getUndirectedIncidentEdge(current);

                    if (currentEdge == null) {
                        return false;
                    }

                    current = getUndirectedConnectedVertex(current, currentEdge);

                    parity++;

                    chosenEdges.remove(currentEdge);
                } while (!start.equals(current));
            }

        } else if (graph.isDirect()) {

            while (!chosenEdges.isEmpty()) {
                Edge currentEdge = chosenEdges.get(0);
                Integer start = currentEdge.getSource();
                Integer current = currentEdge.getTarget();

                chosenEdges.remove(currentEdge);
                parity++;

                // this loop is used in case when we suddenly came to start,
                // but not all edges were used
                do {
                    currentEdge = getDirectedIncidentEdge(current);

                    if (currentEdge == null) {
                        return false;
                    }

                    current = currentEdge.getTarget();

                    parity++;

                    chosenEdges.remove(currentEdge);
                } while (!start.equals(current));
            }

        }

        return parity % 2 != 0;
    }
}