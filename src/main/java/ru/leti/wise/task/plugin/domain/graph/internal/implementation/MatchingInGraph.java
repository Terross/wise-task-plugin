package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class MatchingInGraph implements GraphCharacteristic {

    // Создание матрицы смежности для графа
    private int[][] createAdjMatrix(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        Map<Integer, Integer> vList = new HashMap<>();
        int numVertices = vertices.size();

        for (int i = 0; i < numVertices; i++) {
            Vertex vertex = vertices.get(i);
            vList.put(vertex.getId(), i);
        }

        int[][] matrix = new int[numVertices][numVertices];
        for (Edge edge : graph.getEdgeList()) {
            int fromIndex = vList.get(edge.getSource());
            int toIndex = vList.get(edge.getTarget());

            // Обрабатываем рёбра для направленных и не направленных графов
            if (graph.isDirect()) {
                matrix[fromIndex][toIndex] = 1;
            } else {
                matrix[fromIndex][toIndex] = 1;
                matrix[toIndex][fromIndex] = 1;
            }
        }
        return matrix;
    }

    // Минор матрицы
    private int[][] minor(int[][] a, int x, int y) {
        int length = a.length - 1;
        int[][] result = new int[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (i < x && j < y) {
                    result[i][j] = a[i][j];
                } else if (i >= x && j < y) {
                    result[i][j] = a[i + 1][j];
                } else if (i < x && j >= y) {
                    result[i][j] = a[i][j + 1];
                } else {
                    result[i][j] = a[i + 1][j + 1];
                }
            }
        }
        return result;
    }

    // Рекурсивный расчет определителя матрицы
    private int perm(int[][] a) {
        if (a.length == 1) {
            return a[0][0];
        } else {
            int sum = 0;
            for (int i = 0; i < a.length; i++) {
                sum += a[0][i] * perm(minor(a, 0, i));
            }
            return sum;
        }
    }

    @Override
    public int run(Graph graph) {
        int[][] matr = createAdjMatrix(graph);
        // Можно вывести матрицу для отладки:
        // System.out.println(Arrays.deepToString(matr));
        return perm(matr);
    }
}
