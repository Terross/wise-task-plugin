package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class CheckThreePaths implements GraphProperty {
    private final ArrayList<Integer> bestPath = new ArrayList<>();
    private final ArrayList<Integer> vertices = new ArrayList<>();
    private final HashMap<Integer, ArrayList<Integer>> adjacencyList = new HashMap<>();

    // generates adjacency list for both cases: UNDIRECTED and DIRECTED graph
    private void getAdjacencyList(boolean isDirect, List<Edge> edges) {
        // заполняем список смежности для всех вершин
        for (Integer vertex : vertices) {
            adjacencyList.put(vertex, new ArrayList<>());
        }

        // добавляем рёбра в список смежности
        for (Edge edge : edges) {
            if (isDirect) { // для направленного графа
                ArrayList<Integer> l = adjacencyList.get(edge.getSource());
                l.add(edge.getTarget());
            } else { // для ненаправленного графа
                ArrayList<Integer> l1 = adjacencyList.get(edge.getSource());
                l1.add(edge.getTarget());

                ArrayList<Integer> l2 = adjacencyList.get(edge.getTarget());
                l2.add(edge.getSource());
            }
        }
    }

    private HashMap<Integer, ArrayList<Integer>> deepCopyHashMap(HashMap<Integer, ArrayList<Integer>> hashMap) {
        if (hashMap == null) {
            return null;
        }

        HashMap<Integer, ArrayList<Integer>> newHashMap = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : hashMap.entrySet()) {
            newHashMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return newHashMap;
    }

    private boolean bfs(Integer start, Integer goal, HashMap<Integer, ArrayList<Integer>> adjList) {
        bestPath.clear();

        Queue<Integer> q = new LinkedList<>();
        HashMap<Integer, Integer> path = new HashMap<>();
        HashMap<Integer, Boolean> visited = new HashMap<>();

        visited.put(start, true);
        q.add(start);

        // продолжаем поиск до нахождения цели
        while (!q.isEmpty() && !visited.getOrDefault(goal, false)) {
            Integer currentVert = q.poll();

            for (Integer neighbor : adjList.getOrDefault(currentVert, null)) {
                if (!visited.getOrDefault(neighbor, false)) {
                    visited.put(neighbor, true);
                    path.put(neighbor, currentVert);
                    q.add(neighbor);
                }
            }
        }

        // восстанавливаем путь
        if (visited.getOrDefault(goal, false)) {
            Integer tmp = goal;
            while (!tmp.equals(start)) {
                bestPath.add(0, path.get(tmp));
                tmp = path.get(tmp);
            }
            bestPath.add(goal);

            return true;
        }

        return false;
    }

    private void deletePath(HashMap<Integer, ArrayList<Integer>> adjList, boolean isDirect) {
        for (int i = 1; i < bestPath.size(); i++) {
            adjList.get(bestPath.get(i - 1)).remove(bestPath.get(i));

            if (!isDirect) {
                adjList.get(bestPath.get(i)).remove(bestPath.get(i - 1));
            }
        }
    }

    private boolean findPaths(Integer start, Integer goal, int count, boolean isDirect) {
        HashMap<Integer, ArrayList<Integer>> newAdjList = deepCopyHashMap(adjacencyList);

        for (int i = 0; i < count; i++) {
            bestPath.clear();

            // использует BFS для поиска самого короткого пути
            if (!bfs(start, goal, newAdjList)) {
                return false;
            }

            // если путь найден, он удаляется
            deletePath(newAdjList, isDirect);
        }

        return true;
    }

    @Override
    public boolean run(Graph graph) {
        vertices.clear();
        bestPath.clear();
        adjacencyList.clear();

        for (Edge edge : graph.getEdgeList()) {
            if (!vertices.contains(edge.getSource())) {
                vertices.add(edge.getSource());
            }
            if (!vertices.contains(edge.getTarget())) {
                vertices.add(edge.getTarget());
            }
        }

        getAdjacencyList(graph.isDirect(), graph.getEdgeList());

        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (i == j) continue;

                int PATH_COUNT = 3;
                if (!findPaths(vertices.get(i), vertices.get(j), PATH_COUNT, graph.isDirect())) {
                    return false;
                }
            }
        }

        return true;
    }
}