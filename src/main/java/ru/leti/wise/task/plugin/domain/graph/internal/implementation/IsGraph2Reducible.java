package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class IsGraph2Reducible implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        for (Vertex vertex : graph.getVertexList()) {
            if (subGraph(vertex.getId(), graph, new HashSet<>())) return false;
        }
        return true;
    }

    private Set<Integer> getVertexMate(int vertexId, Graph graph, Set<Integer> used) {
        return graph.getEdgeList().stream()
                .filter(edge -> edge.getSource() == vertexId || edge.getTarget() == vertexId)
                .map(edge -> edge.getSource() == vertexId ? edge.getTarget() : edge.getSource())
                .filter(v -> !used.contains(v))
                .collect(Collectors.toSet());
    }

    private boolean subGraph(int vertexId, Graph graph, Set<Integer> used) {
        used.add(vertexId);
        int minDegree = 2;

        for (Integer ver : used) {
            Set<Integer> subGraphVertex = getVertexMate(ver, graph, new HashSet<>());
            int tmpMin = subGraphVertex.size();
            for (Integer v : subGraphVertex) {
                if (!used.contains(v)) tmpMin -= 1;
            }
            if (tmpMin < minDegree) minDegree = tmpMin;
        }
        if (minDegree > 1) return true;

        for (Integer v : getVertexMate(vertexId, graph, used)) {
            if (subGraph(v, graph, new HashSet<>(used))) return true;
        }
        return false;
    }
}
