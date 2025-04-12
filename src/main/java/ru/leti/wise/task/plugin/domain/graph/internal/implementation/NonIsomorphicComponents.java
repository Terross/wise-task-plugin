package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class NonIsomorphicComponents implements GraphProperty {

    class Node {
        int value;
        List<Node> neighbors;

        public Node(int value) {
            this.value = value;
            this.neighbors = new ArrayList<>();
        }

        public void addNeighbor(Node neighbor) {
            this.neighbors.add(neighbor);
        }
    }

    class Graphs {
        List<Node> nodes;

        public Graphs(int numberOfNodes) {
            this.nodes = new ArrayList<>(numberOfNodes);
            for (int i = 0; i < numberOfNodes; i++) {
                this.nodes.add(new Node(i + 1));
            }
        }

        public void addEdge(int startNode, int endNode) {
            this.nodes.get(startNode - 1).addNeighbor(this.nodes.get(endNode - 1));
            this.nodes.get(endNode - 1).addNeighbor(this.nodes.get(startNode - 1));
        }

        public List<List<Node>> findComponents() {
            List<List<Node>> components = new ArrayList<>();
            boolean[] visited = new boolean[nodes.size()];
            Arrays.fill(visited, false);

            for (Node node : nodes) {
                if (!visited[node.value - 1]) {
                    List<Node> component = new ArrayList<>();
                    Queue<Node> queue = new LinkedList<>();
                    queue.add(node);
                    while (!queue.isEmpty()) {
                        Node current = queue.remove();
                        if (!visited[current.value - 1]) {
                            visited[current.value - 1] = true;
                            component.add(current);
                            for (Node neighbor : current.neighbors) {
                                queue.add(neighbor);
                            }
                        }
                    }
                    components.add(component);
                }
            }
            return components;
        }
    }

    @Override
    public boolean run(Graph graph) {
        var edges = graph.getEdgeList();
        Map<Integer, Integer> indVertex = new HashMap<>();
        int j = 1;

        for (Edge edge : edges) {
            int from = edge.getSource();
            int to = edge.getTarget();

            if (!indVertex.containsKey(from)) {
                indVertex.put(from, j);
                j++;
            }
            if (!indVertex.containsKey(to)) {
                indVertex.put(to, j);
                j++;
            }
        }

        Graphs gr = new Graphs(graph.getVertexCount());
        for (Edge edge : edges) {
            gr.addEdge(indVertex.get(edge.getSource()), indVertex.get(edge.getTarget()));
        }

        List<List<Node>> components = gr.findComponents();
        for (List<Node> component : components) {
            component.forEach(c -> System.out.print('+'));
            System.out.println();
        }

        if (components.size() <= 1) {
            return false;
        }

        for (int i = 0; i < components.size() - 1; i++) {
            for (int k = i + 1; k < components.size(); k++) {
                List<Node> comp1 = components.get(i);
                List<Node> comp2 = components.get(k);

                if (comp1.size() == comp2.size()) {
                    List<Integer> compI1 = new ArrayList<>();
                    List<Integer> compI2 = new ArrayList<>();

                    for (Node node : comp1) {
                        compI1.add(node.neighbors.size());
                    }
                    for (Node node : comp2) {
                        compI2.add(node.neighbors.size());
                    }

                    Collections.sort(compI1);
                    Collections.sort(compI2);

                    if (compI1.equals(compI2)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }
}
