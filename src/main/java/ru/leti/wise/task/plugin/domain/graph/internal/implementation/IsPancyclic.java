package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

@Component
public class IsPancyclic implements GraphProperty {

    @Override
    public boolean run(Graph graph) {
        if (graph.isDirect()) {
            return false;
        }
        if (graph.getVertexCount() < 3) {
            return false;
        }
        Vector<Edge> mst = find_MST(graph);
        Vector<Vector<Edge>> basis = get_Basic_of_Cycles(graph, mst);
        Vector<Integer> answer = generateBinaryCombinations(graph, basis, basis.size());
        for (int i = 2; i < graph.getVertexCount(); i++) {
            if (answer.get(i) == 0) {
                return false;
            }
        }
        return true;
    }

    private List<Integer> getListVertices(Graph graph) {
        List<Integer> vertices = new ArrayList<>();
        for (Vertex vertex : graph.getVertexList()) {
            vertices.add(vertex.getId());
        }
        return vertices;
    }

    // Метод для нахождения базисных циклов
    private void single_dfs(List<Integer> verts, int current, List<Edge> edges, Vector<Edge> cycle, boolean[] visited_vert, boolean[] visited_edge, int start) {
        int currentIndex = verts.indexOf(current);
        visited_vert[currentIndex] = true;
        for (Edge edge : edges) {
            int edgeIndex = edges.indexOf(edge);
            if (!visited_edge[edgeIndex]) {
                if (edge.getSource() == current) {
                    int toIndex = verts.indexOf(edge.getTarget());
                    if (edge.getTarget() == start) {
                        cycle.add(edge);
                        return;
                    } else if (!visited_vert[toIndex]) {
                        visited_edge[edgeIndex] = true;
                        cycle.add(edge);
                        single_dfs(verts, edge.getTarget(), edges, cycle, visited_vert, visited_edge, start);
                        if (cycle.contains(edge)) {
                            return;
                        }
                    }
                } else if (edge.getTarget() == current) {
                    int fromIndex = verts.indexOf(edge.getSource());
                    if (edge.getSource() == start) {
                        cycle.add(edge);
                        return;
                    } else if (!visited_vert[fromIndex]) {
                        visited_edge[edgeIndex] = true;
                        cycle.add(edge);
                        single_dfs(verts, edge.getSource(), edges, cycle, visited_vert, visited_edge, start);
                        if (cycle.contains(edge)) {
                            return;
                        }
                    }
                }
            }
        }
        visited_vert[currentIndex] = false;
        if (!cycle.isEmpty()) {
            cycle.remove(cycle.size() - 1);
        }
    }

    private Vector<Edge> find_MST(Graph graph) {
        Vector<Edge> MST = new Vector<>();
        List<Integer> verts = getListVertices(graph);
        int[] colors = new int[graph.getVertexCount()];
        for (int i = 0; i < graph.getVertexCount(); i++) {
            colors[i] = i;
        }
        for (Edge edge : graph.getEdgeList()) {
            if (colors[verts.indexOf(edge.getSource())] != colors[verts.indexOf(edge.getTarget())]) {
                MST.add(edge);
                for (int i = 0; i < graph.getVertexCount(); i++) {
                    if (colors[i] == colors[verts.indexOf(edge.getTarget())]) {
                        colors[i] = colors[verts.indexOf(edge.getSource())];
                    }
                }
            }
        }
        return MST;
    }

    private Vector<Vector<Edge>> get_Basic_of_Cycles(Graph graph, Vector<Edge> mst) {
        List<Edge> all_edges = graph.getEdgeList();
        Vector<Vector<Edge>> basis = new Vector<>();
        List<Integer> verts = getListVertices(graph);
        for (Edge edge : all_edges) {
            if (!mst.contains(edge)) {
                Vector<Edge> check = new Vector<>(mst);
                check.add(edge);
                boolean[] visited = new boolean[check.size()];
                Vector<Edge> new_cycle = new Vector<>();
                boolean[] visited_verts = new boolean[verts.size()];
                single_dfs(verts, edge.getSource(), check, new_cycle, visited_verts, visited, edge.getSource());
                basis.add(new_cycle);
            }
        }
        return basis;
    }

    private void xor(Vector<Integer> a, Vector<Integer> b) {
        for (int i = 0; i < a.size(); i++) {
            a.set(i, a.get(i) ^ b.get(i));
        }
    }

    private Vector<Integer> generateBinaryCombinations(Graph graph, Vector<Vector<Edge>> basis, int n) {
        int totalCombinations = 1 << n;  // 2^n combinations
        Vector<Vector<Integer>> basis_bin = new Vector<>();
        List<Edge> all_edges = graph.getEdgeList();
        Vector<Integer> length_of_cycles = new Vector<>(graph.getVertexCount());
        for (int i = 0; i < graph.getVertexCount(); i++) {
            length_of_cycles.add(0);
        }
        // Представление циклов в виде бинарной последовательности - вхождение ребер.
        for (int i = 0; i < basis.size(); i++) {
            Vector<Integer> bin = new Vector<>(n);
            for (int k = 0; k < all_edges.size(); k++) {
                bin.add(0);
            }
            for (int j = 0; j < all_edges.size(); j++) {
                if (basis.get(i).contains(all_edges.get(j))) {
                    bin.set(j, 1);
                } else {
                    bin.set(j, 0);
                }
            }
            basis_bin.add(bin);
        }
        Vector<Integer> base = new Vector<>(n);
        for (int k = 0; k < all_edges.size(); k++) {
            base.add(0);
        }
        int count = 0;
        // Перебор всех возможных комбинаций из базисов.
        for (int i = 0; i < totalCombinations; i++) {
            for (int k = 0; k < all_edges.size(); k++) {
                base.set(k, 0);
            }
            String binaryString = Integer.toBinaryString(i);
            while (binaryString.length() < n) {
                binaryString = "0" + binaryString;
            }
            for (int j = 0; j < binaryString.length(); j++) {
                if (binaryString.charAt(j) == '1') {
                    xor(base, basis_bin.get(j));
                }
            }
            for (int k = 0; k < all_edges.size(); k++) {
                if (base.get(k) == 1) count += 1;
            }
            if (count == 0 || count > graph.getVertexCount()) {
                count = 0;
                continue;
            }
            length_of_cycles.set(count - 1, length_of_cycles.get(count - 1) + 1);
            count = 0;
        }
        return length_of_cycles;
    }
}
