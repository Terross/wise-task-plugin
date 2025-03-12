/*
 * Проверка, является ли сумма степеней двух любых вершин графа больше или равна числу вершин графа.
 *  */

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
public class SumDegIsMoreOrEqVertexCount implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        get_degrees(graph);

        int min_degree1 = Integer.MAX_VALUE;
        int min_degree2 = Integer.MAX_VALUE;

        List<Integer> degrees_list = new ArrayList<Integer>(degrees.values());

        for (int i = 0; i < degrees_list.size(); i++) {
            if (min_degree1 > degrees_list.get(i)) {
                min_degree1 = degrees_list.get(i);
            }
            else if(min_degree2 > degrees_list.get(i)) {
                min_degree2 = degrees_list.get(i);
            }
        }

        return min_degree1 + min_degree2 > graph.getVertexCount();
    }

    private HashMap <Integer, Integer> degrees = new HashMap<>();

    private void get_degrees(Graph graph) {
        for (Vertex vertex: graph.getVertexList()) {
            degrees.put(vertex.getId(), 0);
        }

        for(Edge edge: graph.getEdgeList()){
            int from = edge.getSource();
            int to = edge.getTarget();
            degrees.put(from, degrees.get(from) + 1);
            degrees.put(to, degrees.get(to) + 1);
        }
    }
}

