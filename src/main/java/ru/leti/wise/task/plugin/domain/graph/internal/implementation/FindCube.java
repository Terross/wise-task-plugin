/*
* Поиск подграфа куба
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class FindCube implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        Map<Integer, Set<Integer>> adjacencyList = getAdjacencyList(graph);
        Map<Integer, Integer> degrees = getDegrees(adjacencyList);
        Set<Integer> visited = new HashSet<>();
        List<Set<Integer>> comps = findConnectedComponents(adjacencyList, visited);
        
        for (Set<Integer> comp : comps) {
            Set<Integer> candidateVertices = new HashSet<>();
            for (int v : comp) {
                if (degrees.get(v) >= 3) {
                    candidateVertices.add(v);
                }
            }
            int n = candidateVertices.size();
            if (n < 8) continue;
            
            List<Integer> candidateList = new ArrayList<>(candidateVertices);
            List<List<Integer>> combinations = new ArrayList<>();
            generateCombinations(candidateList, 0, new ArrayList<>(), combinations, 8);
            
            for (List<Integer> combo : combinations) {
                if (isCube(combo, adjacencyList)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCube(List<Integer> combo, Map<Integer, Set<Integer>> adj) {
        Set<Integer> comboSet = new HashSet<>(combo);
        int edgeCount = 0;
        int[] degInSub = new int[8];
        Map<Integer, Integer> vertexToIndex = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            vertexToIndex.put(combo.get(i), i);
        }

        for (int i = 0; i < 8; i++) {
            int u = combo.get(i);
            for (int v : adj.get(u)) {
                if (comboSet.contains(v) && u < v) {
                    edgeCount++;
                }
                if (comboSet.contains(v)) {
                    degInSub[i]++;
                }
            }
        }
        
        if (edgeCount != 12) return false;
        for (int d : degInSub) {
            if (d != 3) return false;
        }

        List<Integer>[] subAdj = new List[8];
        for (int i = 0; i < 8; i++) {
            subAdj[i] = new ArrayList<>();
        }
        
        for (int i = 0; i < 8; i++) {
            int u = combo.get(i);
            for (int j = i + 1; j < 8; j++) {
                int v = combo.get(j);
                if (adj.get(u).contains(v)) {
                    subAdj[i].add(j);
                    subAdj[j].add(i);
                }
            }
        }

        int[] color = new int[8];
        Queue<Integer> queue = new LinkedList<>();
        color[0] = 1;
        queue.add(0);
        int visitedCount = 1;
        boolean isBipartite = true;
        
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : subAdj[u]) {
                if (color[v] == 0) {
                    color[v] = 3 - color[u];
                    visitedCount++;
                    queue.add(v);
                } else if (color[v] == color[u]) {
                    isBipartite = false;
                }
            }
        }
        
        if (visitedCount != 8 || !isBipartite) return false;

        int color1Count = 0;
        for (int c : color) {
            if (c == 1) color1Count++;
        }
        return color1Count == 4;
    }

    private void generateCombinations(List<Integer> list, int start, List<Integer> current, 
                                     List<List<Integer>> result, int k) {
        if (k == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i <= list.size() - k; i++) {
            current.add(list.get(i));
            generateCombinations(list, i + 1, current, result, k - 1);
            current.remove(current.size() - 1);
        }
    }

    private Map<Integer, Set<Integer>> getAdjacencyList(Graph graph) {
        Map<Integer, Set<Integer>> adjacencyList = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new HashSet<>());
        }
        for (Edge edge : graph.getEdgeList()) {
            int u = edge.getSource();
            int v = edge.getTarget();
            adjacencyList.get(u).add(v);
            if (u != v) {
                adjacencyList.get(v).add(u);
            }
        }
        return adjacencyList;
    }

    private Map<Integer, Integer> getDegrees(Map<Integer, Set<Integer>> adjacencyList) {
        Map<Integer, Integer> degrees = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : adjacencyList.entrySet()) {
            degrees.put(entry.getKey(), entry.getValue().size());
        }
        return degrees;
    }

    private List<Set<Integer>> findConnectedComponents(Map<Integer, Set<Integer>> adjacencyList, Set<Integer> visited) {
        List<Set<Integer>> components = new ArrayList<>();
        for (int vertex : adjacencyList.keySet()) {
            if (!visited.contains(vertex)) {
                Set<Integer> comp = new HashSet<>();
                dfs(adjacencyList, vertex, comp, visited);
                components.add(comp);
            }
        }
        return components;
    }

    private void dfs(Map<Integer, Set<Integer>> adj, int v, Set<Integer> comp, Set<Integer> visited) {
        visited.add(v);
        comp.add(v);
        for (int neighbor : adj.get(v)) {
            if (!visited.contains(neighbor)) {
                dfs(adj, neighbor, comp, visited);
            }
        }
    }
}