import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

public class MaxClique implements GraphCharacteristic {

    @Override
    public int run(Graph graph) {
        try {
            List<Edge> edges = graph.getEdgeList();
            String[][] ver = new String[edges.size()][2];

            // Получаем все ребра графа
            for (int i = 0; i < edges.size(); i++) {
                ver[i][0] = String.valueOf(edges.get(i).getSource());
                ver[i][1] = String.valueOf(edges.get(i).getTarget());
            }

            Set<String> verticesSet = new HashSet<>();
            for (String[] edge : ver) {
                verticesSet.add(edge[0]);
                verticesSet.add(edge[1]);
            }

            List<String> verticesList = new ArrayList<>(verticesSet);

            // 2. Маппируем вершины на индексы
            Map<String, Integer> vertexIndexMap = new HashMap<>();
            for (int i = 0; i < verticesList.size(); i++) {
                vertexIndexMap.put(verticesList.get(i), i);
            }

            // 3. Инициализируем матрицу смежности
            int[][] adjacencyMatrix = new int[verticesList.size()][verticesList.size()];

            // 4. Заполняем матрицу смежности
            for (String[] edge : ver) {
                String fromVertex = edge[0];
                String toVertex = edge[1];
                int fromIndex = vertexIndexMap.get(fromVertex);
                int toIndex = vertexIndexMap.get(toVertex);
                adjacencyMatrix[fromIndex][toIndex] = 1;
                adjacencyMatrix[toIndex][fromIndex] = 1;
            }

            return maxClique(verticesList.size(), adjacencyMatrix);
        } catch (Exception e) {
            return -1;
        }
    }

    static boolean isClique(int[][] graph, int[] subset, int k) {
        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                if (graph[subset[i]][subset[j]] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    static int maxClique(int n, int[][] graph) {
        int maxSize = 0;
        for (int i = 0; i < (1 << n); i++) { // генерируем все подмножества
            int[] subset = new int[n];
            int subsetSize = 0;
            for (int j = 0; j < n; j++) { // проходим по всем вершинам и проверяем
                if ((i & (1 << j)) > 0) {
                    subset[subsetSize++] = j;
                }
            }
            if (isClique(graph, subset, subsetSize)) {
                maxSize = Math.max(maxSize, subsetSize);
            }
        }
        return maxSize;
    }
}
