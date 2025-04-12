package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class CheckTeit implements GraphProperty {
    private Map<Vertex, List<Edge>> getVerticesEdges(Graph graph) {
        Map<Vertex, List<Edge>> res = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            res.put(vertex, new ArrayList<>());
            for (Edge edge : graph.getEdgeList()) {
                if (edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId()) {
                    res.get(vertex).add(edge);
                }
            }
        }
        return res;
    }

    @Override
    public boolean run(Graph graph) {
        Map<Vertex, List<Edge>> map = getVerticesEdges(graph);
        for (List<Edge> edges : map.values()) {
            for (int i = 0; i < edges.size() - 1; i++) {
                for (int j = i + 1; j < edges.size(); j++) {
                    if (edges.get(i).getColor() == edges.get(j).getColor()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}