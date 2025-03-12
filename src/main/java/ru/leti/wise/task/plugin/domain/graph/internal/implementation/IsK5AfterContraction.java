/*
* Проверка того, что указаны ребра, после стягивания которых заданный непланарный граф превращается в граф К5.
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsK5AfterContraction implements GraphProperty {

    private HashMap<Integer, HashSet<Integer>> contractedGraph;

    private void AddEdge(int fromVertex, int toVertex){
        if(contractedGraph.containsKey(fromVertex)) {
            contractedGraph.get(fromVertex).add(toVertex);
        }
        else{
            contractedGraph.put(fromVertex, new HashSet<>());
            contractedGraph.get(fromVertex).add(toVertex);
        }
    }

    @Override
    public boolean run(Graph graph) {
        if(graph.getVertexCount() < 5 || graph.getEdgeCount() < 10)
            return false;

        contractedGraph = new HashMap<>();
        HashSet<Edge> removedEdges = new HashSet<>();

        for(Edge edge: graph.getEdgeList()){
            var fromVertex = edge.getSource();
            var toVertex = edge.getTarget();

            if(edge.getColor().equals(Color.GRAY)){
                AddEdge(fromVertex, toVertex);
                AddEdge(toVertex, fromVertex);
            }
            else{
                removedEdges.add(edge);
            }
        }

        for(Vertex vertex: graph.getVertexList()){
            if(!contractedGraph.containsKey(vertex.getId())) {
                contractedGraph.put(vertex.getId(), new HashSet<>());
            }
        }

        for(var edge: removedEdges){
            var fromVertex = edge.getSource();
            var toVertex = edge.getTarget();

            if(fromVertex != toVertex) {
                if (!contractedGraph.get(fromVertex).contains(toVertex)) {
                    if(contractedGraph.containsKey(fromVertex) && contractedGraph.containsKey(toVertex)) {
                        contractedGraph.get(toVertex).addAll(contractedGraph.get(fromVertex));
                        contractedGraph.keySet().remove(fromVertex);
                    }
                    for (var vertex : contractedGraph.keySet()) {
                        if(contractedGraph.get(vertex).contains(fromVertex)) {
                            contractedGraph.get(vertex).remove(fromVertex);
                            contractedGraph.get(vertex).add(toVertex);
                        }
                    }
                    for (var tmp: removedEdges){
                        if(tmp.getSource() == fromVertex){
                            tmp.setSource(toVertex);
                        }
                        if(tmp.getTarget() == fromVertex){
                            tmp.setTarget(toVertex);
                        }
                    }
                }
            }
        }

        if(contractedGraph.size() == 5){
            for(var vertex: contractedGraph.keySet()){
                contractedGraph.get(vertex).remove(vertex);
                if(contractedGraph.get(vertex).size() != 4){
                    return false;
                }
            }
            return true;
        }
        return false;

    }
}