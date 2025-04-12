package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class Simple2ways implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        var edges = graph.getEdgeList();
        Map<String, Integer> ind_vertex = new HashMap<>();
        int j = 1;

        for (Edge edge : edges) {
            if (!ind_vertex.containsKey(String.valueOf(edge.getSource()))) {
                ind_vertex.put(String.valueOf(edge.getSource()), j++);
            }
            if (!ind_vertex.containsKey(String.valueOf(edge.getTarget()))) {
                ind_vertex.put(String.valueOf(edge.getTarget()), j++);
            }
        }

        Graphs gr = new Graphs(graph.getVertexCount());
        for (Edge edge : edges) {
            if (graph.isDirect()) {
                gr.addEdge(ind_vertex.get(String.valueOf(edge.getSource())), ind_vertex.get(String.valueOf(edge.getTarget())));
            } else {
                gr.addEdge(ind_vertex.get(String.valueOf(edge.getSource())), ind_vertex.get(String.valueOf(edge.getTarget())));
                gr.addEdge(ind_vertex.get(String.valueOf(edge.getTarget())), ind_vertex.get(String.valueOf(edge.getSource())));
            }
        }

        if (graph.isDirect()) {
            for (int i = 1; i <= graph.getVertexCount(); i++) {
                for (int v = graph.getVertexCount(); v >= i; v--) {
                    if (i != v) {
                        List<List<Integer>> paths = gr.findPaths(i, v);
                        if (paths.isEmpty()) {
                            return false;
                        } else {
                            List<List<Integer>> match = findMatchingArrays(paths);
                            if (match.isEmpty()) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        } else {
            for (int i = 1; i <= graph.getVertexCount(); i++) {
                for (int v = i + 1; v <= graph.getVertexCount(); v++) {
                    if (i != v) {
                        List<List<Integer>> paths = gr.findPaths(i, v);
                        if (paths.isEmpty()) {
                            return false;
                        } else {
                            List<List<Integer>> match = findMatchingArrays(paths);
                            if (match.isEmpty()) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    private static List<List<Integer>> findMatchingArrays(List<List<Integer>> arrays) {
        List<List<Integer>> matchingArrays = new ArrayList<>();
        for (int i = 0; i < arrays.size(); i++) {
            for (int j = i + 1; j < arrays.size(); j++) {
                if (isMatchingArray(arrays.get(i), arrays.get(j))) {
                    matchingArrays.add(arrays.get(i));
                    matchingArrays.add(arrays.get(j));
                    return matchingArrays;
                }
            }
        }
        return matchingArrays;
    }

    private static boolean isMatchingArray(List<Integer> array1, List<Integer> array2) {
        for (int i = 1; i < array1.size() - 1; i++) {
            if (array2.contains(array1.get(i))) {
                return false;
            }
        }
        return true;
    }

    static class Node {
        int value;
        List<Node> neighbors;

        public Node(int value) {
            this.value = value;
            this.neighbors = new ArrayList<>();
        }

        public void addNeighbor(Node neighbor) {
            this.neighbors.add(neighbor);
        }
    }

    static class Graphs {
        List<Node> nodes;

        public Graphs(int numberOfNodes) {
            this.nodes = new ArrayList<>(numberOfNodes);
            for (int i = 0; i < numberOfNodes; i++) {
                this.nodes.add(new Node(i + 1));
            }
        }

        public void addEdge(int startNode, int endNode) {
            this.nodes.get(startNode - 1).addNeighbor(this.nodes.get(endNode - 1));
        }

        public List<List<Integer>> findPaths(int startNode, int endNode) {
            List<List<Integer>> paths = new ArrayList<>();
            Set<Set<Integer>> visitedEdges = new HashSet<>();
            List<Integer> currentPath = new ArrayList<>();
            dfs(this.nodes.get(startNode - 1), endNode, currentPath, paths, visitedEdges);
            return paths;
        }

        private void dfs(Node currentNode, int endNode, List<Integer> currentPath, List<List<Integer>> paths, Set<Set<Integer>> visitedEdges) {
            currentPath.add(currentNode.value);

            if (currentNode.value == endNode) {
                paths.add(new ArrayList<>(currentPath));
            }

            for (Node neighbor : currentNode.neighbors) {
                Set<Integer> edge = new HashSet<>(Arrays.asList(currentNode.value, neighbor.value));

                if (!visitedEdges.contains(edge)) {
                    visitedEdges.add(edge);
                    dfs(neighbor, endNode, currentPath, paths, visitedEdges);
                    visitedEdges.remove(edge);
                }
            }

            currentPath.remove(currentPath.size() - 1);
        }
    }
}
