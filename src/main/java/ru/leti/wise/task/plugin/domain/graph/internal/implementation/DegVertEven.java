/*
* Степени всех вершин графа четные
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class DegVertEven implements GraphProperty {
    public Map<Integer, List<Integer>> getAdjacencyList(Graph graph) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        graph.getVertexList().forEach(vertex -> adjacencyList.put(vertex.getId(), new ArrayList<>()));

        for (Edge edge : graph.getEdgeList()) {
            int key = edge.getSource();
            int value = edge.getTarget();
            adjacencyList.get(key).add(value);
            if (!graph.isDirect())
                adjacencyList.get(value).add(key);
        }

        return adjacencyList;
    }
    @Override
    public boolean run(Graph graph) {
        Map<Integer, List<Integer>> adjacencyList = getAdjacencyList(graph);
        for (int vertex:adjacencyList.keySet()){
            if (adjacencyList.get(vertex).size() %2 != 0){
                return false;
            }
        }
        return true;

    }
}