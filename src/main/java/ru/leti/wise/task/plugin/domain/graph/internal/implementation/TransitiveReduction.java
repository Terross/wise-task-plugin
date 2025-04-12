/*
* проверяет, является ли выделенный подграф транзитивным сокращением.
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class TransitiveReduction implements GraphProperty {

    public class AdjacencyMatrix {

        private int[] vertexes;
        private int[][] matrix;

        private int getIndexVertex(int vertex) {
            for (int i = 0; i < vertexes.length; i++)
                if (vertexes[i] == vertex)
                    return i;
            return -1;
        }

        public AdjacencyMatrix(final ru.leti.wise.task.graph.model.Graph graph) {
            vertexes = graph.getVertexList()
                    .stream()
                    .mapToInt(Vertex::getId)
                    .toArray();
            Arrays.sort(vertexes);
            matrix = new int[vertexes.length][vertexes.length];

            for (int i = 0; i < vertexes.length; ++i)
                for (int j = 0; j < vertexes.length; ++j)
                    matrix[i][j] = 0;

            for (Edge edge : graph.getEdgeList())
                matrix[getIndexVertex(edge.getSource())][getIndexVertex(edge.getTarget())] = 1;


            if (!graph.isDirect())
                for (int i = 0; i < vertexes.length; ++i)
                    for (int j = 0; j < i; ++j) {
                        matrix[i][j] = matrix[j][i] + matrix[i][j];
                        matrix[j][i] = matrix[i][j];
                    }

        }

        public AdjacencyMatrix(final ru.leti.wise.task.graph.model.Graph graph, final ru.leti.wise.task.graph.model.Color color) {
            vertexes = graph.getVertexList()
                    .stream()
                    .mapToInt(Vertex::getId)
                    .toArray();
            Arrays.sort(vertexes);
            matrix = new int[vertexes.length][vertexes.length];

            for (int i = 0; i < vertexes.length; ++i)
                for (int j = 0; j < vertexes.length; ++j)
                    matrix[i][j] = 0;

            for (Edge edge : graph.getEdgeList())
                if (edge.getColor() == color)
                    matrix[getIndexVertex(edge.getSource())][getIndexVertex(edge.getTarget())] = 1;

            if (!graph.isDirect())
                for (int i = 0; i < vertexes.length; ++i)
                    for (int j = 0; j < i; ++j) {
                        matrix[i][j] = matrix[j][i] + matrix[i][j];
                        matrix[j][i] = matrix[i][j];
                    }
        }

        public AdjacencyMatrix(TransitiveReduction.AdjacencyMatrix other) {
            vertexes = Arrays.copyOf(other.vertexes, other.vertexes.length);
            matrix = other.getMatrix();
        }

        public TransitiveReduction.AdjacencyMatrix clone() { return new TransitiveReduction.AdjacencyMatrix(this); }

        public boolean isValidMatrix() { return vertexes != null; }

        public int[][] getMatrix() {
            int[][] tmp = new int[matrix.length][matrix.length];
            for (int i = 0; i < matrix.length; ++i)
                tmp[i] = Arrays.copyOf(matrix[i], matrix[i].length);
            return tmp;
        }

        public boolean setMatrix(int[][] other_matrix) {
            if (other_matrix.length != matrix.length)
                return false;

            for (int i = 0; i < matrix.length; ++i)
                if (other_matrix[i].length != matrix[i].length)
                    return false;

            for (int i = 0; i < matrix.length; ++i)
                matrix[i] = Arrays.copyOf(other_matrix[i], other_matrix[i].length);

            return true;
        }

        public boolean equals(TransitiveReduction.AdjacencyMatrix adjacencyMatrix) {
            if (vertexes.length != adjacencyMatrix.vertexes.length)
                return false;

            for (int i = 0; i < vertexes.length; ++i) {
                if (vertexes[i] != adjacencyMatrix.vertexes[i])
                    return false;
                for (int j = 0; j < vertexes.length; ++j)
                    if (matrix[i][j] != adjacencyMatrix.matrix[i][j])
                        return false;
            }

            return true;
        }

    }

    public AdjacencyMatrix transitiveClosure(final AdjacencyMatrix adjacency_matrix) {
        int[][] matrix = adjacency_matrix.getMatrix();

        for (int k = 0; k < matrix.length; ++k)
            for (int i = 0; i < matrix.length; ++i)
                for (int j = 0; j < matrix.length; ++j)
                    matrix[i][j] = matrix[i][j] | (matrix[i][k] & matrix[k][j]);

        AdjacencyMatrix transitive_closure_matrix = new AdjacencyMatrix(adjacency_matrix);
        transitive_closure_matrix.setMatrix(matrix);

        return transitive_closure_matrix;
    }

    public boolean isMinReduction(final AdjacencyMatrix general_transitive_matrix,
                                         AdjacencyMatrix other_adjacency_matrix) {
        int[][] tmp_matrix = other_adjacency_matrix.getMatrix();

        for (int i = 0; i < tmp_matrix.length; ++i)
            for (int j = 0; j < tmp_matrix.length; ++j)
                if (tmp_matrix[i][j] == 1) {
                    tmp_matrix[i][j] = 0;
                    other_adjacency_matrix.setMatrix(tmp_matrix);
                    if (general_transitive_matrix.equals(transitiveClosure(other_adjacency_matrix)))
                        return false;
                    tmp_matrix[i][j] = 1;
                }

        other_adjacency_matrix.setMatrix(tmp_matrix);
        return true;
    }

    private final List<Color> colors = Arrays.asList(Color.RED, Color.BLUE, Color.GREEN);

    private List<AdjacencyMatrix> SplitToColor(final Graph graph) {
        List<AdjacencyMatrix> list = new ArrayList<>();

        for (Color color : colors) {
            AdjacencyMatrix tmp = new AdjacencyMatrix(graph, color);
            if (tmp.isValidMatrix())
                list.add(tmp);
        }

        return list;
    }

    @Override
    public boolean run(Graph graph) {
        List<AdjacencyMatrix> list_adjacency_matrix = SplitToColor(graph);
        AdjacencyMatrix general_transitive_matrix = transitiveClosure(new AdjacencyMatrix(graph));

        for (AdjacencyMatrix other_adjacency_matrix : list_adjacency_matrix)
            if (general_transitive_matrix.equals(transitiveClosure(other_adjacency_matrix)))
                if (isMinReduction(general_transitive_matrix, other_adjacency_matrix))
                    return true;

        return false;
    }
}
