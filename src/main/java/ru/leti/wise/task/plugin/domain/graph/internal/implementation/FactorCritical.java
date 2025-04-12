/*
 * Этот код реализует алгоритм проверки фактор-критичности графа.
 *
 * В контексте теории графов фактор-критический граф — это неориентированный граф,
 * в котором удаление любой вершины делает граф содержащим совершенное паросочетание (matching).
 *
 *  Другими словами, после удаления любой вершины оставшийся граф имеет паросочетание, покрывающее все его вершины.
 *  */
 
 package ru.leti.wise.task.plugin.domain.graph.internal.implementation;
 
 import org.springframework.stereotype.Component;
 import ru.leti.wise.task.graph.model.Graph;
 import ru.leti.wise.task.graph.model.Edge;
 import ru.leti.wise.task.graph.model.Vertex;
 import ru.leti.wise.task.graph.model.Color;
 import ru.leti.wise.task.plugin.graph.GraphProperty;
 
 import java.util.*;
 import java.util.stream.Collectors;
 
 @Component
 public class FactorCritical implements GraphProperty {
 
     @Override
     public boolean run(Graph graph) {
 
         List<Edge> array1 = new ArrayList<>();
         Map<Integer, Vertex> map1= new HashMap<>();
         List<Vertex> vert = graph.getVertexList();
         Map<Integer, Vertex> map2 = vert.stream()
                 .collect(Collectors.toMap(Vertex::getId, v -> v));
 
         if (graph.getVertexCount() % 2 == 0)
             return false;
 
         List<Edge> array2 = graph.getEdgeList();
 
         if (array2 == null)
             return false;
 
         for(int vertex: map2.keySet()) {
             map1.putAll(map2);
             map1.remove(vertex);
             for (Edge edge: array2)
                 if (!(edge.getTarget() == vertex || edge.getSource() == vertex))
                     array1.add(new Edge(edge.getSource(), edge.getTarget(), edge.getColor(), edge.getWeight(), edge.getLabel()));
 
             Graph graph1= new Graph(map1.size(), array1.size(), graph.isDirect(), array1, new ArrayList<Vertex>(map1.values()));
             if (2 * MaxMatching(graph1) != map1.size())
                 return false;
 
             map1.clear();
             array1.clear();
         }
         return true;
     }
 
 
     public Integer MaxMatching(Graph graph) {
 
         List<Edge> M = new ArrayList<>();
         List<Vertex> vert = graph.getVertexList();
         Map<Integer, Vertex> map = vert.stream()
                 .collect(Collectors.toMap(Vertex::getId, v -> v));
 
         List<Edge> array = graph.getEdgeList();
 
         for (Edge edge : array) {
             map.get(edge.getTarget()).setWeight(0);
             map.get(edge.getSource()).setWeight(0);}
 
         for (Edge edge : array) {
             map.get(edge.getTarget()).setWeight(map.get(edge.getTarget()).getWeight() + 1);
             map.get(edge.getSource()).setWeight(map.get(edge.getSource()).getWeight() + 1);
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