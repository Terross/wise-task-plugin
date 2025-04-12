package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class AntiTransitive implements GraphProperty {

    public boolean run(Graph graph) {

        List<Edge> edges = graph.getEdgeList();
        List<Vertex> vertices = graph.getVertexList();
        int vertexCount = vertices.size();
        boolean[][] matrix = new boolean[vertexCount][vertexCount];

        Map<Integer, Integer> vertexIndexMap = new HashMap<>();
        for (int i = 0; i < vertexCount; i++) {
            vertexIndexMap.put(vertices.get(i).getId(), i);
        }

        // Заполнение матрицы смежности
        for (Edge edge : edges) {
            int from = edge.getSource();
            int to = edge.getTarget();

            int indexFrom = vertexIndexMap.get(from);
            int indexTo = vertexIndexMap.get(to);

            matrix[indexFrom][indexTo] = true;

            if (!graph.isDirect()) {
                matrix[indexTo][indexFrom] = true;
            }
        }

        // Проверка антитранзитивности
        for (int i = 0; i < vertexCount; ++i) {
            for (int j = 0; j < vertexCount; ++j) {
                if (matrix[i][j]) {
                    for (int k = 0; k < vertexCount; ++k) {
                        if (matrix[j][k] && i != k) {
                            if (matrix[i][k]) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
