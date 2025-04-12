package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class TransitiveReductions implements GraphCharacteristic {

    private Optional<Edge> findEdge(final List<Edge> edges, final int src, final int dst) {
        for (Edge edge : edges) {
            int from = edge.getSource();
            int to = edge.getTarget();

            if ((dst == to && src == from) || (dst == from && src == to)) {
                return Optional.of(edge);
            }
        }
        return Optional.empty();
    }

    private Integer countTransitiveReductions(final Graph graph, final int startIndex) {
        final int edgeCount = graph.getEdgeCount();
        final int vertexCount = graph.getVertexCount();

        if (vertexCount == 0 || edgeCount + 1 <= vertexCount) {
            return 1;
        }

        final HashMap<Integer, Vertex> vertices = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            vertices.put(vertex.getId(), vertex);
        }

        final List<Edge> edges = graph.getEdgeList();
        int reductions = 0;

        for (int i = startIndex; i < edgeCount; ++i) {
            Edge edge = edges.get(i);

            int a = edge.getSource();
            int b = edge.getTarget();

            for (final Integer c : vertices.keySet()) {
                if (findEdge(edges, a, c).isEmpty() || findEdge(edges, c, b).isEmpty()) {
                    continue;
                }

                List<Edge> subgraphEdges = new ArrayList<>(edges);
                subgraphEdges.remove(i);

                reductions += countTransitiveReductions(
                        new Graph(graph.getVertexCount(),
                                graph.getEdgeCount() - 1,
                                graph.isDirect(),
                                subgraphEdges,
                                new ArrayList<>(vertices.values())),
                        i);

                break;
            }
        }

        return reductions == 0 ? 1 : reductions;
    }

    @Override
    public int run(final Graph graph) {
        List<Edge> subgraphEdges = new ArrayList<>();
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == edge.getTarget()) {
                continue;
            }
            subgraphEdges.add(edge);
        }

        return countTransitiveReductions(
                new Graph(graph.getVertexCount(),
                        subgraphEdges.size(),
                        graph.isDirect(),
                        subgraphEdges,
                        graph.getVertexList()),
                0);
    }
}
