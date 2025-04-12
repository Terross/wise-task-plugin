package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.awt.*;
import java.util.*;
import java.util.List;


@Component
public class OneValent implements GraphProperty {
    private final ArrayList<Edge> chosenEdges = new ArrayList<>();
    private final Set<Integer> chosenVertexes = new HashSet<>();

    // собираем выделенные ребра
    private void getChosenEdges(Graph graph) {
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getColor() == Color.RED) {
                chosenEdges.add(edge);
            }
        }
    }

    // собираем выделенные вершины
    private void getChosenVertexes(Graph graph) {
        for (Edge edge : chosenEdges) {
            chosenVertexes.add(edge.getSource());
            chosenVertexes.add(edge.getTarget());
        }
    }

    // проверяем на вхождение и выход ребер, сверяем количесвто вершин
    private boolean isOneValent(int vertexIndex) {
        int from = 0;
        int to = 0;
        for (Edge edge : chosenEdges) {
            if (edge.getSource() == vertexIndex) {
                from++;
            } else if (edge.getTarget() == vertexIndex) {
                to++;
            }
        }

        // если одно вхождение и один выход - возврат 1
        return from == 1 && to == 1;
    }

    public boolean run(Graph graph) {
        //найдем выделенные ребра
        getChosenEdges(graph);
        //выделенные вершины
        getChosenVertexes(graph);

        // проверка, что кол-во выделенных вершин = кол-ву вершин исход графа
        if (chosenVertexes.size() != graph.getVertexCount()) {
            return false;
        }

        // проверка каждой вершины на одновалентность
        for (Integer vertex : chosenVertexes) {
            if (!isOneValent(vertex)) {
                return false;
            }
        }
        return true;
    }

}