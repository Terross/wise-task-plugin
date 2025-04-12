package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;
import java.util.stream.Collectors;

// Проверка графа на 3-редуцируемость
@Component
public class IsGraph3Reducible implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        for (Vertex vertex : graph.getVertexList()) {
            if (subgraphCheck(vertex.getId(), graph, new HashSet<>())) {
                return false;
            }
        }
        return true;
    }

    // Проверяет связность и минимальную степень подграфа для вершины
    private boolean subgraphCheck(int vertexId, Graph graph, Set<Integer> usedVertex) {
        usedVertex.add(vertexId);
        int minDegree = 3;

        // Проверяем смежные вершины
        for (Integer ver : usedVertex) {
            Set<Integer> subGraphVertex = getVertexMate(ver, graph, new HashSet<>());
            int tmpMin = subGraphVertex.size();
            for (Integer v : subGraphVertex) {
                if (!usedVertex.contains(v)) tmpMin -= 1;
            }
            if (tmpMin < minDegree) minDegree = tmpMin;
        }

        // Если минимальная степень > 2, то граф не 3-редуцируем
        if (minDegree > 2) {
            return true;
        }

        // Рекурсивно проверяем остальные вершины
        for (Integer newVertex : getVertexMate(vertexId, graph, usedVertex)) {
            if (subgraphCheck(newVertex, graph, new HashSet<>(usedVertex))) return true;
        }
        return false;
    }

    // Возвращает множество смежных вершин для заданной вершины
    private Set<Integer> getVertexMate(int vertexId, Graph graph, Set<Integer> usedVertex) {
        return graph.getEdgeList().stream()
                .filter(edge -> edge.getSource() == vertexId || edge.getTarget() == vertexId)
                .map(edge -> edge.getSource() == vertexId ? edge.getTarget() : edge.getSource())
                .filter(v -> !usedVertex.contains(v))
                .collect(Collectors.toSet());
    }
}
