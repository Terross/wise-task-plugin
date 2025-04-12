package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsIsomorphicK5withoutSelectedEdges implements GraphProperty {
    final static int VERTEX_COUNT = 5;
    final static int MIN_EDGES_COUNT = (VERTEX_COUNT * (VERTEX_COUNT - 1));

    @Override
    public boolean run(Graph graph) {
        if (graph.getVertexCount() < VERTEX_COUNT) {
            return false;
        }
        if (graph.getEdgeCount() < MIN_EDGES_COUNT) {
            return false;
        }

        // Список смежности
        Map<Integer, Set<Integer>> adjacencyList = new HashMap<>();
        Map<Integer, Set<Integer>> reversedAdjacencyList = new HashMap<>();

        Set<Integer> vertexIds = new HashSet<>();
        for (Vertex vertex : graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new HashSet<>());
            reversedAdjacencyList.put(vertex.getId(), new HashSet<>());
            vertexIds.add(vertex.getId());
        }

        // Заполняем список смежности для рёбер с серым цветом
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getColor() != Color.GRAY) {
                continue;
            }
            int startId = edge.getSource();
            int endId = edge.getTarget();
            adjacencyList.get(startId).add(endId);
            reversedAdjacencyList.get(endId).add(startId);
        }

        HashSet<Integer> updatedVertexIds = new HashSet<>();
        for (Integer vertexId : vertexIds) {
            var outgoingVertices = adjacencyList.get(vertexId);
            var incomingVertices = reversedAdjacencyList.get(vertexId);

            // Если у вершины больше 1 исходящих или входящих рёбер
            if (outgoingVertices.size() > 1 || incomingVertices.size() > 1) {
                if (updatedVertexIds.size() >= VERTEX_COUNT) {
                    return false;
                }
                updatedVertexIds.add(vertexId);
                continue;
            }

            // Если у вершины нет исходящих или входящих рёбер
            if (outgoingVertices.isEmpty() || incomingVertices.isEmpty()) {
                return false;
            }

            for (Integer fromId : incomingVertices) {
                for (Integer toId : outgoingVertices) {
                    if (adjacencyList.get(fromId).contains(toId)
                        || reversedAdjacencyList.get(toId).contains(fromId)) {
                        return false;
                    }

                    // Перемещаем рёбра
                    adjacencyList.get(fromId).remove(vertexId);
                    adjacencyList.get(fromId).add(toId);
                    reversedAdjacencyList.get(toId).remove(vertexId);
                    reversedAdjacencyList.get(toId).add(fromId);
                }
            }
        }

        if (updatedVertexIds.size() < VERTEX_COUNT) {
            return false;
        }

        // Проверяем условие на количество рёбер у обновлённых вершин
        for (Integer vertexId : updatedVertexIds) {
            var outgoingVertices = adjacencyList.get(vertexId);
            var incomingVertices = reversedAdjacencyList.get(vertexId);
            if (incomingVertices.size() != VERTEX_COUNT - 1 || outgoingVertices.size() != VERTEX_COUNT - 1) {
                return false;
            }
        }

        return true;
    }
}
