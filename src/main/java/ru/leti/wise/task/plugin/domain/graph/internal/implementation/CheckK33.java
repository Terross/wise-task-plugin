package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class CheckK33 implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        Predicate<Edge> cf = (Edge e) -> e.getColor() == Color.RED;

        List<Edge> removedEdges = graph.getEdgeList().stream()
                .filter(cf)
                .collect(Collectors.toCollection(LinkedList::new));

        List<Edge> restedEdges = graph.getEdgeList().stream()
                .filter(e -> !cf.test(e))
                .collect(Collectors.toCollection(LinkedList::new));

        Set<Integer> checked = new HashSet<>();
        Set<Set<Integer>> groups = new HashSet<>();

        for (Edge e : removedEdges) {
            Set<Integer> group = null;
            for (Set<Integer> g : groups) {
                if (g.contains(e.getSource()) || g.contains(e.getTarget())) {
                    group = g;
                    break;
                }
            }

            if (group == null) {
                group = new HashSet<>();
                groups.add(group);
            }

            group.add(e.getSource());
            checked.add(e.getSource());
            group.add(e.getTarget());
            checked.add(e.getTarget());
        }

        for (Vertex v : graph.getVertexList()) {
            if (!checked.contains(v.getId())) {
                groups.add(new HashSet<>(Collections.singleton(v.getId())));
            }
        }

        Map<Integer, Integer> newVertices = new HashMap<>();
        int newId = 1;
        for (Set<Integer> g : groups) {
            for (int el : g) {
                newVertices.put(el, newId);
            }
            newId++;
        }

        for (Edge e : restedEdges) {
            e.setSource(newVertices.get(e.getSource()));
            e.setTarget(newVertices.get(e.getTarget()));
        }

        restedEdges.removeIf(e -> restedEdges.stream()
                .anyMatch(edge -> e != edge && e.getSource() == edge.getSource() && e.getTarget() == edge.getTarget()));

        if (groups.size() != 6 || restedEdges.size() != 9) return false;

        Set<Integer> left = new HashSet<>(Collections.singleton(restedEdges.get(0).getSource()));
        Set<Integer> right = new HashSet<>();

        for (Edge e : restedEdges) {
            if (left.contains(e.getSource())) right.add(e.getTarget());
            else if (left.contains(e.getTarget())) right.add(e.getSource());
        }
        for (Edge e : restedEdges) {
            if (left.contains(e.getSource())) right.add(e.getTarget());
            else if (left.contains(e.getTarget())) right.add(e.getSource());
            else if (right.contains(e.getSource())) left.add(e.getTarget());
            else if (right.contains(e.getTarget())) left.add(e.getSource());
        }

        return (left.size() == 3 && right.size() == 3);
    }
}
