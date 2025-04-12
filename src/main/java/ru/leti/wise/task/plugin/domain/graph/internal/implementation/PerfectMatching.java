package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class PerfectMatching implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        List<Edge> edgesToCheck = getEdgesToCheck(graph);
        return isPerfectMatching(graph, edgesToCheck);
    }

    private boolean isPerfectMatching(Graph graph, List<Edge> edgesToCheck) {
        for (var vertex : graph.getVertexList()) {
            boolean incidentToSingleEdge = false;
            for (Edge edge : edgesToCheck) {
                if (edge.getSource() == vertex.getId() || edge.getTarget() == vertex.getId()) {
                    if (incidentToSingleEdge) {
                        return false;
                    }
                    incidentToSingleEdge = true;
                }
            }
            if (!incidentToSingleEdge) {
                return false;
            }
        }
        return true;
    }

    private List<Edge> getEdgesToCheck(Graph graph) {
        List<Edge> edgesToCheck = new ArrayList<>();
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getColor() != Color.BLUE) { //был серый -- заменила на синий
                edgesToCheck.add(edge);
            }
        }
        return edgesToCheck;
    }
}
