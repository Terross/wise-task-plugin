/*
* Поиск циклов длинны 3
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;

import java.util.*;

@Component
public class TriangleChecker implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        if (graph.isDirect()) {
            return false;
        }

        Map<Integer, Set<Integer>> adjacencyList = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new HashSet<>());
        }

        for (Edge e : graph.getEdgeList()) {
            int u = e.getSource();
            int v = e.getTarget();
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        for (Edge edge : graph.getEdgeList()) {
            Set<Integer> N_u = adjacencyList.get(edge.getSource());
            Set<Integer> N_v = adjacencyList.get(edge.getTarget());

            for (int w : N_u) {
                if (N_v.contains(w)) {
                    return true;
                }
            }
        }
        return false;
    }
}
