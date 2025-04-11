package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class K4Subdivision implements GraphProperty {

    private class Node {
        int value;
        List<Node> neighbors;

        public Node(int value) {
            this.value = value;
            this.neighbors = new ArrayList<>();
        }

        public void addNeighbor(Node neighbor) {
            this.neighbors.add(neighbor);
        }

        public List<Node> getNeighbors() {
            return neighbors;
        }
    }

    private class GraphStructure {
        List<Node> nodes;

        public GraphStructure(int numberOfNodes) {
            this.nodes = new ArrayList<>();
            for (int i = 0; i < numberOfNodes; i++) {
                this.nodes.add(new Node(i));
            }
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void addEdge(int startNode, int endNode) {
            this.nodes.get(startNode).addNeighbor(this.nodes.get(endNode));
            this.nodes.get(endNode).addNeighbor(this.nodes.get(startNode));
        }

        public List<List<Node>> findComponents() {
            List<List<Node>> components = new ArrayList<>();
            boolean[] visited = new boolean[nodes.size()];
            Arrays.fill(visited, false);

            for (Node node : nodes) {
                if (!visited[node.value]) {
                    List<Node> component = new ArrayList<>();
                    Queue<Node> queue = new LinkedList<>();
                    queue.add(node);

                    while (!queue.isEmpty()) {
                        Node current = queue.poll();
                        if (!visited[current.value]) {
                            visited[current.value] = true;
                            component.add(current);
                            queue.addAll(current.getNeighbors());
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
        List<Edge> edges = graph.getEdgeList();
        Map<Integer, Integer> vertexIndexMap = new HashMap<>();
        int index = 0;

        for (Edge edge : edges) {
            vertexIndexMap.putIfAbsent(edge.getSource(), index++);
            vertexIndexMap.putIfAbsent(edge.getTarget(), index++);
        }

        GraphStructure graphStructure = new GraphStructure(graph.getVertexCount());

        for (Edge edge : edges) {
            graphStructure.addEdge(vertexIndexMap.get(edge.getSource()), vertexIndexMap.get(edge.getTarget()));
        }

        List<List<Node>> components = graphStructure.findComponents();
        if (components.size() != 1) {
            return false;
        }

        int countDegreeThree = 0;
        int countDegreeTwo = 0;

        for (Node node : graphStructure.getNodes()) {
            int degree = node.getNeighbors().size();
            if (degree == 3) {
                countDegreeThree++;
            } else if (degree == 2) {
                countDegreeTwo++;
            } else {
                return false;
            }
        }

        return countDegreeThree == 4;
    }
}
