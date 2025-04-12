/*
Окружение графа (натуральное число с нулем)

Окружение графа — это длина его наибольшего простого цикла.

Цикл — это последовательность вершин a1a2 . . . an и различных рёбер e1, . . . , en графа G,
где ei = aiai+1 для всех i ∈ [1..n] (мы считаем, что an+1 = a1).

Цикл называется простым, если все вершины a1, . . . , an различны.
*/

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;

@Component
public class LargestCycle implements GraphCharacteristic {
    Integer max_length = 0;
    Set<Integer> used_vert = new HashSet<>();
    Set<Edge> used_edges = new HashSet<>();

    @Override
    public int run(Graph graph) {

        if (graph.isDirect()) {
            // Перебираем все вершины графа
            for (Vertex vertex : graph.getVertexList()) {
                used_vert.clear();
                bnb_dir(graph, vertex.getId(), vertex.getId());
            }
        }
        else {
            // Перебираем все вершины графа
            for (Vertex vertex : graph.getVertexList()) {
                used_vert.clear();
                used_edges.clear();
                bnb_undir(graph, vertex.getId(), vertex.getId());
            }
        }

        return max_length;
    }


    // Рекурсивная функция перебора
    public void bnb_undir(Graph graph, int startVertexId, int currentVertexId) {
        // Если мы вернулись в стартовую вершину и это не начало пути
        if (used_vert.size() > 1 && startVertexId == currentVertexId) {
            max_length = Math.max(used_vert.size(), max_length);
            return;
        }

        if (!used_vert.contains(currentVertexId)) {
            used_vert.add(currentVertexId);
            for (Edge edge : graph.getEdgeList()) {
                if (!used_edges.contains(edge)) {
                    if (edge.getSource() == currentVertexId) {
                        used_edges.add(edge);
                        int nextVertexId = edge.getTarget();
                        used_vert.add(currentVertexId);
                        bnb_undir(graph, startVertexId, nextVertexId);
                        used_edges.remove(edge);
                    }
                    if (edge.getTarget() == currentVertexId) {
                        used_edges.add(edge);
                        int nextVertexId = edge.getSource();
                        bnb_undir(graph, startVertexId, nextVertexId);
                        used_edges.remove(edge);
                    }
                }
            }
            used_vert.remove(currentVertexId);
        }
    }

    // Рекурсивная функция перебора
    public void bnb_dir(Graph graph, int startVertexId, int currentVertexId) {
        // Если мы вернулись в стартовую вершину и это не начало пути
        if (used_vert.size() > 1 && startVertexId == currentVertexId) {
            max_length = Math.max(used_vert.size(), max_length);
            return;
        }

        if (!used_vert.contains(currentVertexId)) {
            used_vert.add(currentVertexId);
            for (Edge edge : graph.getEdgeList()) {
                if (edge.getSource() == currentVertexId) {
                    int nextVertexId = edge.getTarget();
                    bnb_dir(graph, startVertexId, nextVertexId);
                }
            }
            used_vert.remove(currentVertexId);
        }
    }
}

