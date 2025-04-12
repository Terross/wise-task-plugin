package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class TwoOddCon implements GraphProperty {

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

    public Map<Vertex, List<Edge>> delete(Graph graph, Map<Vertex, List<Edge>> map, Vertex vertex) {
        List<Edge> edges = map.get(vertex);  // edges must be deleted
        map.remove(vertex);  // delete vertex
        for (Vertex id : map.keySet()) {  // delete needed edges
            for (int i = 0; i < map.get(id).size(); i++) {
                for (Edge edge : edges) {
                    if (i >= map.get(id).size()) { break; }
                    if (edge.equals(map.get(id).get(i))) {
                        map.get(id).remove(i);
                    }
                }
            }
        }
        return map;
    }

    public List<Vertex> neighbors(Graph graph, HashMap<Vertex, List<Edge>> map, Vertex vertex) {
        List<Vertex> neighbors = new ArrayList<>();
        Map<Integer, Vertex> vertices = new HashMap<>();
        for (Vertex v : graph.getVertexList()) {
            vertices.put(v.getId(), v);
        }

        for (Integer id : getListEdges(map, vertex)) {
            neighbors.add(vertices.get(id));
        }
        return neighbors;
    }

    public HashMap<Vertex, Double> BellmanFord(Graph graph, HashMap<Vertex, List<Edge>> map, Vertex start) {
        // Initialization
        List<Vertex> listVertices = new ArrayList<>(map.keySet());
        HashMap<Vertex, Double> D = new HashMap<>();
        for (Vertex u : listVertices) {
            D.put(u, Double.POSITIVE_INFINITY);
        }
        D.put(start, 0.0);
        List<Vertex> listNeighbors = neighbors(graph, map, start);
        for (Vertex u : listNeighbors) {
            D.put(u, 1.0);
        }

        // Process
        for (int i = 0; i < listVertices.size() - 2; i++) {
            for (Vertex u : listVertices) {
                listNeighbors = neighbors(graph, map, u);
                for (Vertex w : listNeighbors) {
                    Double tmp = Math.min(D.get(u), D.get(w) + 1);
                    D.put(u, tmp);
                }
            }
        }
        return D;
    }

    @Override
    public boolean run(Graph graph) {
        for (Vertex v1 : graph.getVertexList()) {  // delete v1
            HashMap<Vertex, List<Edge>> map = new HashMap<>(getVerticesEdges(graph));
            delete(graph, map, v1);
            for (Vertex v : map.keySet()) {  // BellmanFord
                HashMap<Vertex, Double> ways = BellmanFord(graph, map, v);
                for (Vertex u : ways.keySet()) {
                    if (ways.get(u).equals(Double.POSITIVE_INFINITY)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
