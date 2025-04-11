package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Color;

import java.util.*;

@Component
public class FindPaws implements GraphProperty {

    public Map<Vertex, List<Edge>> getVerticesEdges(Graph graph) {
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

    public List<Integer> getListEdges(Map<Vertex, List<Edge>> map, Vertex vertex) {
        List<Integer> localList = new ArrayList<>();
        for (Edge edge : map.get(vertex)) {
            if (!localList.contains(edge.getTarget()) && edge.getTarget() != vertex.getId()) {
                localList.add(edge.getTarget());
            }
            if (!localList.contains(edge.getSource()) && edge.getSource() != vertex.getId()) {
                localList.add(edge.getSource());
            }
        }
        return localList;
    }

    public boolean cmp(List<Integer> localList, List<Integer> inductionList, Vertex vertex) {
        for (Integer idFrom : localList) {
            for (Integer idTo : inductionList) {
                if (idFrom.equals(idTo) && !idTo.equals(vertex.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean check(List<Integer> corrects, List<Integer> reds, Graph graph) {
        if (corrects.size() != reds.size()) {
            return false;
        }
        return corrects.containsAll(reds) && reds.containsAll(corrects);
    }

    @Override
    public boolean run(Graph graph) {
        Map<Vertex, List<Edge>> map = getVerticesEdges(graph);
        List<Integer> corrects = new ArrayList<>();
        List<Integer> reds = new ArrayList<>();

        for (Vertex vertex : graph.getVertexList()) {
            if (vertex.getColor() == Color.RED) {
                reds.add(vertex.getId());
            }

            boolean flag = true;
            if (map.get(vertex).size() == 3) {
                List<Integer> localList = getListEdges(map, vertex);

                for (Edge edge : map.get(vertex)) {
                    for (Vertex neighbor : graph.getVertexList()) {
                        if (neighbor.getId() == edge.getSource() || neighbor.getId() == edge.getTarget()) {
                            if (!vertex.equals(neighbor) && map.get(neighbor).size() > 1) {
                                List<Integer> inductionList = getListEdges(map, neighbor);
                                if (!cmp(localList, inductionList, vertex)) {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (flag && !corrects.contains(vertex.getId())) {
                    corrects.add(vertex.getId());
                }
            }
        }
        return check(corrects, reds, graph);
    }
}
