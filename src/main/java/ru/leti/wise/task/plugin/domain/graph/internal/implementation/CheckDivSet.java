/*
* Разделяющие множества
*
*Основная задача кода — определить, можно ли разделить вершины графа на два или более множества,
* удовлетворяющих определённым условиям.
* В частности, код проверяет, можно ли разделить граф на множества,
* где вершины одного цвета (например, красные) играют роль разделителей.
*
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
public class CheckDivSet implements GraphProperty {

    public class ColorNode
    {
        public int color = 0;
        public Vertex node;
        public ColorNode(Vertex node) {
            this.node = node;
        }
    }

    public ColorNode getColorNode(int edge, Vector<ColorNode> set_color_node)
    {
        for (ColorNode temp_color : set_color_node)
            if (temp_color.node.getId() == edge)
                return temp_color;

        return null;
    }

    public void workNode(Graph graph, ColorNode color_node, Vector<ColorNode> set_color_node, boolean with_red_node, int number_color){
        for (Edge edge : graph.getEdgeList()) {
            if (edge.getSource() == color_node.node.getId()) {
                ColorNode temp_color = getColorNode(edge.getTarget(), set_color_node);
                dfs(temp_color, graph, number_color, set_color_node, with_red_node);
            }
            else if (edge.getTarget() == color_node.node.getId()) {
                ColorNode temp_color = getColorNode(edge.getSource(), set_color_node);
                dfs(temp_color, graph, number_color, set_color_node, with_red_node);
            }
        }
    }

    HashSet<Integer> unic_set = new HashSet<>();
    HashSet<ColorNode> visited = new HashSet<>();
    public void dfs(ColorNode color_node, Graph graph, int number_color, Vector<ColorNode> set_color_node, boolean with_red_node)
    {
        if (with_red_node) {
            if (color_node.color == 0 && color_node.node.getColor() != Color.RED) {
                color_node.color = number_color;
                workNode(graph, color_node, set_color_node, with_red_node, number_color);
            }
        }
        else {
            if (!visited.contains(color_node)) {
                visited.add(color_node);
                unic_set.add(color_node.color);
                workNode(graph, color_node, set_color_node, with_red_node, number_color);
            }
        }
    }
    Vector<ColorNode> set_color_nodes = new Vector<>();
    public boolean solve(Graph graph, boolean with_red_node)
    {
        int number_color = 0;
        if (with_red_node) {
            for (Vertex ver : graph.getVertexList()) {
                ColorNode temp = new ColorNode(ver);
                set_color_nodes.add(temp);
            }
        }
        for (ColorNode node : set_color_nodes) {
            if (with_red_node) {
                if (node.color == 0 && node.node.getColor() != Color.RED) {
                    number_color = number_color + 1;
                    dfs(node, graph, number_color, set_color_nodes, with_red_node);
                }
            }
            else {
                if (!visited.contains(node)) {
                    dfs(node, graph, number_color, set_color_nodes, with_red_node);
                    if (unic_set.size() > 2){
                        return true;
                    }
                    unic_set.clear();
                }
            }
        }
        return false;
    }

    @Override
    public boolean run(Graph graph) {
        if (graph.isDirect())
            return false;

        solve(graph, true);
        return solve(graph, false);
    }
}