/*
* Этот код реализует алгоритм поиска максимального паросочетания в графе.
*
* В теории графов паросочетание — это подмножество рёбер графа,
* в котором никакие два рёбра не инцидентны одной и той же вершине.
*
* Максимальное паросочетание — это паросочетание наибольшего возможного размера
* (то есть содержащее максимальное количество рёбер).
*/

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MaximalMatching implements GraphCharacteristic {
    @Override
    public int run(Graph graph) {

        List<Edge> M = new ArrayList<>();
        List<Vertex> vert = graph.getVertexList();
        Map<Integer, Vertex> map = vert.stream()
                .collect(Collectors.toMap(Vertex::getId, v -> v));

        List<Edge> array = graph.getEdgeList();

        for (Edge edge : array) {
            map.get(edge.getTarget()).setWeight(0);
            map.get(edge.getSource()).setWeight(0);}

        for (Edge edge : array) {
            map.get(edge.getTarget()).setWeight(map.get(edge.getSource()).getWeight() + 1);
            map.get(edge.getSource()).setWeight(map.get(edge.getTarget()).getWeight() + 1);
        }


        boolean sorted = false;
        Edge temp;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < array.size()-1; i++) {
                int sumVWi = map.get(array.get(i).getTarget()).getWeight() + map.get(array.get(i).getSource()).getWeight();
                int sumVWi1 = map.get(array.get(i + 1).getTarget()).getWeight() + map.get(array.get(i + 1).getSource()).getWeight();
                if (sumVWi > sumVWi1) {
                    temp = array.get(i);
                    array.set(i, array.get(i + 1));
                    array.set(i + 1, temp);
                    sorted = false;
                }
            }
        }


        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getColor() != Color.RED) {
                M.add(array.get(i));
                array.get(i).setColor(Color.BLUE);
                for (Edge edge : array) {
                    if (edge == array.get(i)) continue;
                    if ((map.get(edge.getTarget()) == (map.get(array.get(i).getTarget()))
                            || map.get(edge.getTarget()) == (map.get(array.get(i).getSource()))
                            || map.get(edge.getSource()) == (map.get(array.get(i).getTarget()))
                            || map.get(edge.getSource()) == (map.get(array.get(i).getSource())))) {
                        edge.setColor(Color.RED);
                    }
                }
            }
        }
        return M.toArray().length;
    }
}
