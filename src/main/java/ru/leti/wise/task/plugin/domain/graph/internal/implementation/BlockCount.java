package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class BlockCount implements GraphCharacteristic {

    private int count = 0;
    private int time = 0;

    @Override
    public int run(Graph graph) {
        if (graph.getEdgeCount() == 0) {
            return 0;
        }
        return countBiconnectedComponents(graph);
    }

    private int countBiconnectedComponents(Graph graph) {
        Map<Integer, Integer> disc = new HashMap<>();
        Map<Integer, Integer> low = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        LinkedList<Edge> stack = new LinkedList<>();

        for (Vertex vertex : graph.getVertexList()) {
            disc.put(vertex.getId(), -1);
            low.put(vertex.getId(), -1);
            parent.put(vertex.getId(), null);
        }

        for (Vertex vertex : graph.getVertexList()) {
            if (disc.get(vertex.getId()) == -1) {
                BCCUtil(graph, vertex, disc, low, stack, parent);
                int j = 0;
                while (!stack.isEmpty()) {
                    j = 1;
                    stack.removeLast();
                }
                if (j == 1) {
                    count++;
                }
            }
        }

        return count;
    }

    private void BCCUtil(Graph graph, Vertex u, Map<Integer, Integer> disc, Map<Integer, Integer> low,
                         LinkedList<Edge> stack, Map<Integer, Integer> parent) {

        disc.put(u.getId(), ++time);
        low.put(u.getId(), time);
        int children = 0;

        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == u.getId() || edge.getTarget() == u.getId()) {
                int vId = (edge.getSource() == u.getId()) ? edge.getTarget() : edge.getSource();
                Vertex v = findVertexById(graph, vId);

                if (disc.get(vId) == -1) {
                    children++;
                    parent.put(vId, u.getId());

                    stack.addLast(edge);
                    BCCUtil(graph, v, disc, low, stack, parent);

                    low.put(u.getId(), Math.min(low.get(u.getId()), low.get(vId)));

                    if ((disc.get(u.getId()) == 1 && children > 1) || (disc.get(u.getId()) > 1 && low.get(vId) >= disc.get(u.getId()))) {
                        while (!stack.isEmpty() && (stack.getLast().getSource() == u.getId() || stack.getLast().getTarget() == vId)) {
                            stack.removeLast();
                        }
                        if (!stack.isEmpty()) {
                            stack.removeLast();
                        }
                        count++;
                    }
                } else if (parent.get(u.getId()) == null || (vId != parent.get(u.getId()) && disc.get(vId) < disc.get(u.getId()))) {
                    low.put(u.getId(), Math.min(low.get(u.getId()), disc.get(vId)));
                    stack.addLast(edge);
                }
            }
        }
    }

    private Vertex findVertexById(Graph graph, int id) {
        for (Vertex v : graph.getVertexList()) {
            if (v.getId() == id) {
                return v;
            }
        }
        throw new NoSuchElementException("Vertex with id " + id + " not found");
    }
}