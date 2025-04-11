package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class MinimalBarrier implements GraphProperty {

    @Override
    public boolean run(Graph graph) {

        if (!isBipartite(graph)) {
            return false; // Граф не двудольный
        }

        Set<Integer> in_barrier = new HashSet<>();
        Set<Integer> not_in_barrier = new HashSet<>();

        // Разделяем вершины на барьер и не барьер
        for (Vertex vertex : graph.getVertexList()) {
            if (vertex.getColor() == Color.RED) {
                in_barrier.add(vertex.getId());
            } else {
                not_in_barrier.add(vertex.getId());
            }
        }

        Map<Integer, Vertex> vertices = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            vertices.put(vertex.getId(), vertex);
        }

        List<Edge> edges = graph.getEdgeList();

        // Строим список смежности
        HashMap<Integer, List<Integer>> adjList = new HashMap<>();
        for (Vertex vertex : vertices.values()) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : edges) {
            adjList.get(edge.getSource()).add(edge.getTarget());
            adjList.get(edge.getTarget()).add(edge.getSource());
        }

        int max_match = findMaxMatchingSize(graph);
        int deficit = graph.getVertexCount() - max_match;

        // Проверка барьера
        int odd_of_original = odd(not_in_barrier, adjList);
        if (odd_of_original - in_barrier.size() != deficit) {
            return false; // Это не барьер
        }

        if (in_barrier.isEmpty()) {
            return true; // Минимальный барьер - пустое множество
        }

        // Проверяем, что при удалении одной вершины из барьера новый odd уменьшается на 1
        for (Integer vertex : in_barrier) {
            Set<Integer> tmp_not_in = new HashSet<>(not_in_barrier);
            tmp_not_in.add(vertex);
            int new_odd = odd(tmp_not_in, adjList);
            if (new_odd == odd_of_original - 1) {
                return false; // Барьер не минимален
            }
        }

        return true; // Барьер минимален
    }

    private static boolean isBipartite(Graph graph) {
        Map<Integer, Vertex> vertices = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            vertices.put(vertex.getId(), vertex);
        }

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> colors = new HashMap<>();
        HashMap<Integer, List<Integer>> adjList = new HashMap<>();

        for (Vertex vertex : vertices.values()) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : graph.getEdgeList()) {
            adjList.get(edge.getSource()).add(edge.getTarget());
            adjList.get(edge.getTarget()).add(edge.getSource());
        }

        for (Integer vertexId : vertices.keySet()) {
            if (queue.isEmpty()) {
                if (visited.contains(vertexId)) {
                    continue; // Если вершина уже была проверена, переходим к следующей
                } else {
                    colors.put(vertexId, 1); // Начинаем новую компоненту связности
                    queue.add(vertexId);
                }
            }

            while (!queue.isEmpty()) {
                Integer current = queue.poll();
                visited.add(current);

                for (Integer neigh : adjList.get(current)) {
                    if (colors.containsKey(neigh)) {
                        if (visited.contains(neigh)) {
                            continue;
                        } else if (colors.get(neigh).equals(colors.get(current))) {
                            return false; // Граф не двудольный
                        }
                    } else {
                        queue.add(neigh);
                        colors.put(neigh, 1 - colors.get(current));
                    }
                }
            }
        }

        return true; // Граф двудольный
    }

    private static int odd(Set<Integer> vertices, HashMap<Integer, List<Integer>> adjList) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        Set<Integer> seen = new HashSet<>();
        int answer = 0;

        for (Integer vertexId : vertices) {
            if (visited.contains(vertexId)) {
                continue; // Если вершина уже была проверена, переходим к следующей
            }
            queue.add(vertexId);
            int tmp = 0;
            while (!queue.isEmpty()) {
                tmp++;
                Integer current = queue.poll();
                visited.add(current);

                for (Integer neigh : adjList.get(current)) {
                    if (!vertices.contains(neigh)) continue; // Проверяем только соседей в множестве vertices
                    if (seen.contains(neigh) || visited.contains(neigh)) {
                        continue;
                    }
                    seen.add(neigh);
                    queue.add(neigh);
                }
            }

            if (tmp % 2 == 1) {
                answer++;
            }
        }

        return answer;
    }

    private static int findMaxMatchingSize(Graph graph) {
        Map<Integer, Integer> matching = new HashMap<>();
        Map<Integer, Vertex> vertices = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            vertices.put(vertex.getId(), vertex);
        }

        List<Edge> edges = graph.getEdgeList();
        HashMap<Integer, List<Integer>> adjList = new HashMap<>();
        for (Vertex vertex : vertices.values()) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : edges) {
            adjList.get(edge.getSource()).add(edge.getTarget());
            adjList.get(edge.getTarget()).add(edge.getSource());
        }

        for (Integer vertex : vertices.keySet()) {
            matching.put(vertex, null);
        }

        boolean flag;
        int answer = 0;

        for (Integer vertex : vertices.keySet()) {
            Set<Integer> visited = new HashSet<>();
            flag = dfs(vertex, adjList, visited, matching);
            if (flag) answer++;
        }

        return answer;
    }

    private static boolean dfs(Integer v, HashMap<Integer, List<Integer>> adjList, Set<Integer> visited, Map<Integer, Integer> matching) {
        if (visited.contains(v)) {
            return false;
        }
        visited.add(v);

        for (Integer neigh : adjList.get(v)) {
            if (matching.get(neigh) == null) {
                matching.put(neigh, v);
                return true;
            } else {
                if (dfs(matching.get(neigh), adjList, visited, matching)) {
                    matching.put(neigh, v);
                    return true;
                }
            }
        }
        return false;
    }
}
