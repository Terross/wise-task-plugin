package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class IsGraphSquared implements GraphProperty {

    // Метод для создания представления графа в виде списка смежности
    private HashMap<Integer, List<Vertex>> createHashMapGraph(List<Vertex> vertices, List<Edge> edges) {
        HashMap<Integer, List<Vertex>> adjList = new HashMap<>();
        for (var vertex : vertices) {
            adjList.put(vertex.getId(), new ArrayList<>());
        }

        for (var edge : edges) {
            adjList.get(edge.getSource()).add(findVertexById(vertices, edge.getTarget()));
        }
        return adjList;
    }

    // Метод для создания транзитивного замыкания
    private HashMap<Integer, List<Vertex>> createHashMapClosure(HashMap<Integer, List<Vertex>> map) {
        HashMap<Integer, List<Vertex>> tranz = new HashMap<>();
        for (var vertex1 : map.keySet()) {
            for (var vertex2 : map.get(vertex1)) {
                for (var vertex3 : map.get(vertex2.getId())) {
                    if (map.get(vertex1).contains(vertex3)) {
                        tranz.putIfAbsent(vertex1, new ArrayList<>());
                        tranz.get(vertex1).add(vertex3);
                    }
                }
            }
        }
        return tranz;
    }

    // Проверка на квадратность графа
    public boolean checkIsSquared(Graph graph) {
        boolean isSquared = true;
        HashMap<Integer, List<Vertex>> map = createHashMapGraph(graph.getVertexList(), graph.getEdgeList());
        HashMap<Integer, List<Vertex>> tranz = createHashMapClosure(map);

        for (var vertex1 : map.keySet()) {
            for (var vertex2 : map.get(vertex1)) {
                for (var vertex3 : map.get(vertex2.getId())) {
                    // Преобразование примитивного типа int
                    if (!map.get(vertex1).contains(vertex3) &&
                            (!tranz.containsKey(vertex1) || !tranz.get(vertex1).contains(vertex2)) &&
                            (!tranz.containsKey(vertex2.getId()) || !tranz.get(vertex2.getId()).contains(vertex3)) &&
                            vertex3.getId() != vertex1) {  // Исправлено на простое сравнение
                        isSquared = false;
                        return isSquared;
                    }
                }
            }
        }
        return isSquared;
    }

    // Поиск вершины по ID
    private Vertex findVertexById(List<Vertex> vertices, int id) {
        return vertices.stream()
                .filter(vertex -> vertex.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Vertex not found"));
    }

    @Override
    public boolean run(Graph graph) {
        return checkIsSquared(graph);
    }
}
