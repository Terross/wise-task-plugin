package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class BlockCheck implements GraphProperty {
    private int time = 0;
    private Stack<Vertex> stack = new Stack<>();

    @Override
    public boolean run(Graph graph) {
        List<Vertex> componentVertices = new ArrayList<>();
        List<Vertex> vertices = graph.getVertexList(); // Используем getVertexList()
        for (Vertex vertex : vertices) {
            if (vertex.getColor() == Color.RED) {
                componentVertices.add(vertex);
            }
        }
        return isBiconnectedComponent(graph, componentVertices);
    }


    public boolean isBiconnectedComponent(Graph graph, List<Vertex> componentQuery) {
        List<Set<Vertex>> components = findBiconnectedComponents(graph);
        Set<Vertex> querySet = new HashSet<>(componentQuery);
        for (Set<Vertex> component : components) {
            if (component.equals(querySet)) {
                return true;
            }
        }
        return false;
    }

    private List<Set<Vertex>> findBiconnectedComponents(Graph graph) {
        Map<Vertex, Integer> disc = new HashMap<>();
        Map<Vertex, Integer> low = new HashMap<>();
        Map<Vertex, Vertex> parent = new HashMap<>();
        List<Set<Vertex>> components = new ArrayList<>();

        for (Vertex v : graph.getVertexList()) {
            if (!disc.containsKey(v)) {
                dfs(v, disc, low, parent, components, graph);
                if (!stack.isEmpty()) {
                    Set<Vertex> remainingComponent = new HashSet<>();
                    while (!stack.isEmpty()) {
                        remainingComponent.add(stack.pop());
                    }
                    components.add(remainingComponent);
                }
            }
        }
        return components;
    }

    private void dfs(Vertex u, Map<Vertex, Integer> disc, Map<Vertex, Integer> low, Map<Vertex, Vertex> parent, List<Set<Vertex>> components, Graph graph) {
        disc.put(u, time);
        low.put(u, time);
        time++;
        stack.push(u);

        for (Vertex v : getAdjVertices(u, graph)) {
            if (!disc.containsKey(v)) {
                parent.put(v, u);
                dfs(v, disc, low, parent, components, graph);

                low.put(u, Math.min(low.get(u), low.get(v)));

                if (low.get(v) >= disc.get(u)) {
                    Set<Vertex> component = new HashSet<>();
                    Vertex w;
                    do {
                        w = stack.pop();
                        component.add(w);
                    } while (!w.equals(v));
                    component.add(u);
                    components.add(component);
                }
            } else if (!v.equals(parent.get(u))) {
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }
    }

    private Set<Vertex> getAdjVertices(Vertex v, Graph graph) {
        Set<Vertex> adjacentVertices = new HashSet<>();
        // Для каждого ребра находим смежные вершины
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == v.getId()) {
                adjacentVertices.add(graph.getVertexList().stream()
                        .filter(vertex -> vertex.getId() == edge.getTarget())
                        .findFirst().orElse(null));
            } else if (edge.getTarget() == v.getId()) {
                adjacentVertices.add(graph.getVertexList().stream()
                        .filter(vertex -> vertex.getId() == edge.getSource())
                        .findFirst().orElse(null));
            }
        }
        return adjacentVertices;
    }
}
