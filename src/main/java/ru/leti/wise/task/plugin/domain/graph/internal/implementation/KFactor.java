/*
* k-factor
*
* Этот код реализует проверку графа на k-факторность (k-factor),
* т.е. проверяет, является ли граф k-регулярным (все вершины имеют одинаковую степень)
* с учетом только рёбер, не окрашенных в серый цвет.
*/

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class KFactor implements GraphProperty {
    @Override
    public boolean run(Graph graph){
        List<Edge> edges=graph.getEdgeList();
        List<Vertex> vert = graph.getVertexList();
        Map<Integer, Vertex> vertices = vert.stream()
                .collect(Collectors.toMap(Vertex::getId, v -> v));

        int k = 0, k1, t = 0;
        if (edges!=null) {
            for (int vertex : vertices.keySet()) {
                k1 = 0;
                for (Edge edge : edges)
                    if (edge.getColor() != Color.GRAY)
                        if (edge.getTarget() == vertex || edge.getSource() == vertex)
                            k1 += 1;

                if (k == 0 && t == 0) {
                    k = k1;
                    t = 1;
                }
                else if (k != k1)
                    return false;

            }
        }
        return true;
    }

}