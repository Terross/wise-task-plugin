package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class ChromaticNumberEq5 implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        if (graph.getVertexCount() < 5) {
            return false;
        }

        List<Edge> edges = graph.getEdgeList();
        List<Vertex> vertices = graph.getVertexList();

        // Сортируем вершины по степени
        List<Integer> sortedVertices = sortVertices(getVertexIds(vertices), edges);

        Map<Integer, Integer> colors = new HashMap<>();
        for (Integer id : getVertexIds(vertices)) {
            colors.put(id, -1);
        }

        int chromaticNum = 0;
        Integer currentV;

        // Основной цикл для расчета хроматического числа
        while (!sortedVertices.isEmpty()) {
            chromaticNum++;
            if (chromaticNum > 5) {
                return false;
            }

            currentV = sortedVertices.remove(0);
            colors.put(currentV, chromaticNum);

            List<Integer> toDelete = new ArrayList<>();
            // Ищем не смежные вершины и проверяем, можно ли покрасить
            for (Integer v : sortedVertices) {
                boolean isAdjacent = false;
                for (Edge edge : edges) {
                    if (edge.getSource() == v || edge.getTarget() == v) {
                        if (edge.getSource() == currentV || edge.getTarget() == currentV) {
                            isAdjacent = true;
                            break;
                        }
                    }
                }

                // Если вершина не смежная и еще не покрашена, пробуем покрасить
                if (!isAdjacent && colors.get(v) == -1) {
                    boolean canFill = true;
                    for (Edge edge : edges) {
                        if ((edge.getSource() == v || edge.getTarget() == v) &&
                                (colors.get(edge.getSource()) == chromaticNum || colors.get(edge.getTarget()) == chromaticNum)) {
                            canFill = false;
                            break;
                        }
                    }

                    if (canFill) {
                        colors.put(v, chromaticNum);
                        toDelete.add(v);
                    }
                }
            }

            // Удаляем покрашенные вершины из списка
            sortedVertices.removeAll(toDelete);
            sortedVertices = update(sortedVertices, edges);
        }

        return chromaticNum == 5;
    }

    // Получение списка идентификаторов вершин
    private List<Integer> getVertexIds(List<Vertex> vertices) {
        List<Integer> ids = new ArrayList<>();
        for (Vertex vertex : vertices) {
            ids.add(vertex.getId());
        }
        return ids;
    }

    // Сортировка вершин по степени
    public List<Integer> sortVertices(List<Integer> vertices, List<Edge> edges) {
        List<Integer> sorted = new ArrayList<>(vertices);

        sorted.sort((v1, v2) -> {
            int degreeDiff = degree(v2, edges) - degree(v1, edges);
            if (degreeDiff == 0) {
                int v1Count = 0, v2Count = 0;
                for (Edge edge : edges) {
                    if (edge.getSource() == v1 || edge.getTarget() == v1) {
                        v1Count += degree(edge.getSource() == v1 ? edge.getTarget() : edge.getSource(), edges);
                    }
                    if (edge.getSource() == v2 || edge.getTarget() == v2) {
                        v2Count += degree(edge.getSource() == v2 ? edge.getTarget() : edge.getSource(), edges);
                    }
                }
                degreeDiff = v2Count - v1Count;
            }
            return Integer.compare(degreeDiff, 0);
        });

        return sorted;
    }

    // Нахождение степени вершины
    public int degree(int id, List<Edge> edges) {
        int degree = 0;
        for (Edge edge : edges) {
            if (edge.getSource() == id || edge.getTarget() == id) {
                degree++;
            }
        }
        return degree;
    }

    // Обновление списка вершин
    public List<Integer> update(List<Integer> vertices, List<Edge> edges) {
        List<Edge> copyEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (vertices.contains(edge.getSource()) && vertices.contains(edge.getTarget())) {
                copyEdges.add(edge);
            }
        }
        return sortVertices(vertices, copyEdges);
    }
}
