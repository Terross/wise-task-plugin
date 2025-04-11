package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;


@Component
public class GraphMinimalTermsContraction implements GraphCharacteristic {

    @Override
    public int run(Graph graph) {

        // Проверяем, является ли граф неориентированным
        if (graph.isDirect()) {
            throw new IllegalArgumentException("Граф должен быть неориентированным");
        }

        // Проверка на взвешенность, отрицательные веса и другие условия
        if (!isWeighted(graph)) {
            throw new IllegalArgumentException("Граф не взвешенный");
        }

        if (!isNonNegative(graph)) {
            throw new IllegalArgumentException("Граф не должен быть отрицательным");
        }

        if (graph.getVertexList() == null || graph.getEdgeList() == null) {
            throw new IllegalArgumentException("Граф не содержит вершин или ребер");
        }

        if (!isConnectedGraph(graph)) {
            throw new IllegalArgumentException("Граф не связный");
        }

        return isMinimallyContractible(graph);
    }

    private Integer isMinimallyContractible(Graph graph) {

        if (graph.getVertexCount() == 0) {
            return 0;
        }

        int vertexCount = graph.getVertexCount();
        List<Edge> edges = graph.getEdgeList();
        List<Vertex> vertices = graph.getVertexList();

        // Приоритетная очередь для выбора ребра с наименьшим весом
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));

        // Начинаем с первой вершины
        Vertex startVertex = vertices.get(0);
        Set<Vertex> inMST = new HashSet<>();
        inMST.add(startVertex);

        for (Edge edge : edges) {
            // Добавляем в приоритетную очередь все ребра, инцидентные начальной вершине
            if (edge.getSource() == startVertex.getId() || edge.getTarget() == startVertex.getId()) {
                pq.add(edge);
            }
        }

        int mstWeight = 0;

        while (!pq.isEmpty() && inMST.size() < vertexCount) {
            // Извлекаем из приоритетной очереди ребро с наименьшим весом
            Edge edge = pq.poll();

            Vertex fromV = graph.getVertexList().get(edge.getSource()); // получаем объект вершины по ID
            Vertex toV = graph.getVertexList().get(edge.getTarget()); // получаем объект вершины по ID

            // Проверяем, образует ли ребро цикл
            if (inMST.contains(fromV) && inMST.contains(toV)) {
                continue;
            }

            mstWeight += edge.getWeight();

            // Определяем, какую из вершин ребра нужно добавить в MST
            Vertex newVertex = inMST.contains(fromV) ? toV : fromV;
            inMST.add(newVertex);

            // Добавляем все ребра новой вершины в очередь приоритетов
            for (Edge nextEdge : graph.getEdgeList()) {
                if (nextEdge.getSource() == newVertex.getId() || nextEdge.getTarget() == newVertex.getId()) {
                    pq.add(nextEdge);
                }
            }
        }


        return mstWeight;
    }


    private boolean isConnectedGraph(Graph graph) {
        Map<Vertex, Boolean> visited = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            visited.put(vertex, false);
        }

        dfs(graph, graph.getVertexList().get(0), visited);

        return !visited.containsValue(false);
    }

    private void dfs(Graph graph, Vertex currentVertex, Map<Vertex, Boolean> visited) {
        visited.put(currentVertex, true);
        for (Edge edge : graph.getEdgeList()) {
            // Проверяем, инцидентно ли текущее ребро текущей вершине
            if (edge.getSource() == currentVertex.getId() && !visited.get(graph.getVertexList().get(edge.getTarget()))) {
                dfs(graph, graph.getVertexList().get(edge.getTarget()), visited);
            }
            if (edge.getTarget() == currentVertex.getId() && !visited.get(graph.getVertexList().get(edge.getSource()))) {
                dfs(graph, graph.getVertexList().get(edge.getSource()), visited);
            }
        }
    }



    private boolean isWeighted(Graph graph) {
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getWeight() == null || edge.getWeight() == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isNonNegative(Graph graph) {
        for (Vertex vertex : graph.getVertexList()) {
            if (vertex.getWeight() != null && vertex.getWeight() < 0) {
                return false;
            }
        }

        for (Edge edge : graph.getEdgeList()) {
            if (edge.getWeight() != null && edge.getWeight() < 0) {
                return false;
            }
        }

        return true;
    }
}
