package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;


@Component
public class Connected_3 implements GraphProperty {

    private int vertexCount;
    private boolean[][] adj;
    private int time;

    @Override
    public boolean run(Graph graph) {
        this.vertexCount = graph.getVertexCount();

        // Создаём матрицу смежности
        adj = new boolean[vertexCount][vertexCount];
        for (Edge edge : graph.getEdgeList()) {
            int from = edge.getSource();
            int to = edge.getTarget();
            adj[from][to] = true;
            adj[to][from] = true;
        }

        return isThreeConnected();
    }

    private boolean isThreeConnected() {
        for (int i = 0; i < vertexCount; i++) {
            int degree = 0;
            for (int j = 0; j < vertexCount; j++) {
                if (adj[i][j]) {
                    degree++;
                }
            }
            if (degree < 3) {
                return false;
            }
        }

        if (!isTwoConnected()) {
            return false;
        }

        for (int u = 0; u < vertexCount; u++) {
            for (int v = u + 1; v < vertexCount; v++) {
                if (!checkTwoConnectedAfterRemoval(u, v)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isTwoConnected() {
        boolean[] visited = new boolean[vertexCount];
        int[] disc = new int[vertexCount];
        int[] low = new int[vertexCount];
        int[] parent = new int[vertexCount];
        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);
        Arrays.fill(parent, -1);

        for (int i = 0; i < vertexCount; i++) {
            if (disc[i] == -1) {
                if (apDFS(i, visited, disc, low, parent)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean apDFS(int u, boolean[] visited, int[] disc, int[] low, int[] parent) {
        boolean isArticulationPoint = false;
        int children = 0;
        visited[u] = true;
        disc[u] = low[u] = ++time;

        for (int v = 0; v < vertexCount; v++) {
            if (adj[u][v]) {
                if (!visited[v]) {
                    children++;
                    parent[v] = u;
                    if (apDFS(v, visited, disc, low, parent)) {
                        return true;
                    }
                    low[u] = Math.min(low[u], low[v]);
                    if (parent[u] == -1 && children > 1) {
                        isArticulationPoint = true;
                    }
                    if (parent[u] != -1 && low[v] >= disc[u]) {
                        isArticulationPoint = true;
                    }
                } else if (v != parent[u]) {
                    low[u] = Math.min(low[u], disc[v]);
                }
            }
        }
        return isArticulationPoint;
    }

    private boolean checkTwoConnectedAfterRemoval(int u, int v) {
        boolean[] visited = new boolean[vertexCount];
        boolean started = false;

        for (int i = 0; i < vertexCount; i++) {
            if (i != u && i != v && !visited[i]) {
                dfs(i, u, v, visited);
                started = true;
                break;
            }
        }

        if (!started) return true;

        for (int i = 0; i < vertexCount; i++) {
            if (i != u && i != v && !visited[i]) {
                return false;
            }
        }
        return true;
    }

    private void dfs(int u, int exclude1, int exclude2, boolean[] visited) {
        visited[u] = true;
        for (int v = 0; v < vertexCount; v++) {
            if (adj[u][v] && v != exclude1 && v != exclude2 && !visited[v]) {
                dfs(v, exclude1, exclude2, visited);
            }
        }
    }
}
