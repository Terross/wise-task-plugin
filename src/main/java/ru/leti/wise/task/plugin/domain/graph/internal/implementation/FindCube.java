/*
* Поиск подграфа куба
* */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.plugin.graph.GraphProperty;

import java.util.*;

@Component
public class FindCube implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        HashMap <Integer, Integer> degrees = getDegrees(graph);
        HashMap<Integer, List<Integer>> adjacencyList = getAdjacencyList(graph);
        Set<Integer> visited = new HashSet<>();
        List<Set<Integer>> comps =  findConectComponents(adjacencyList,visited);
        Set<Set<Integer>> compSet = new HashSet<>(comps);
        List<Set<Integer>> cubes = new ArrayList<>();
        Set<Integer> combined = new HashSet<>();
        Set<Set<Integer>> combinations = new HashSet<>();
        for (Set<Integer> comp : compSet) {
            generateCombinations(comp, index, combined, combinations);
        };
        int count = 0;
        for(Set<Integer> comb : compSet){
            count = 0;
            for (int id : comb){
                if(degrees.get(id).equals(3)){
                    count++;
                }
            }
            if(count == comb.size() && count >=2){
                cubes.add(comb);
            }
        }
        if(cubes.isEmpty()){
            return false;
        }else{
        return true;}
    }
    Set<Set<Integer>> pathSet = new HashSet<>();

    public HashMap<Integer, List<Integer>> getAdjacencyList(Graph graph) {
        HashMap<Integer, List<Integer>> adjacencyList = new HashMap<>();
        for(Vertex vertex: graph.getVertexList()) {
            adjacencyList.put(vertex.getId(), new ArrayList<>());
        }

        for (Edge edge : graph.getEdgeList()) {
            int key = edge.getSource();
            int value = edge.getTarget();
            adjacencyList.get(key).add(value);
        }

        return adjacencyList;
    }
    public HashMap <Integer, Integer> getDegrees(Graph graph){
        HashMap <Integer, Integer> degrees = new HashMap<>();
        for (Vertex vertex : graph.getVertexList()) {
            degrees.put(vertex.getId(), 0);
        }
        for (Edge edge : graph.getEdgeList()){
            int from = edge.getSource();
            int to = edge.getTarget();
            Integer tmp_from = degrees.get(from) + 1;
            degrees.put(from, tmp_from);

            Integer tmp_to = degrees.get(to) + 1;
            degrees.put(to, tmp_to);
        }
        return degrees;
    }

    private  Set<Integer> dfs(HashMap<Integer,List<Integer>> adjacencyList, int vertex, Set<Integer> visited){
        visited.add(vertex);
        for(int tmp:  adjacencyList.get(vertex)){
            if(!visited.contains(tmp)){
                dfs(adjacencyList, tmp, visited);
            }
        }
        return visited;
    }

    private List<Set<Integer>> findConectComponents(HashMap<Integer,List<Integer>> adjacencyList,Set<Integer> visited ){
        List<Set<Integer>> components = new ArrayList<>();
        Set <Integer> path = new HashSet<>();
        for(int vertex: adjacencyList.keySet()){
            if(!visited.contains(vertex)){
                Set<Integer> tmp_visited = new HashSet<>();
                tmp_visited.add(vertex);
                if(!components.contains(tmp_visited)){
                components.add(tmp_visited);}
                dfs(adjacencyList, vertex, tmp_visited);
            }
        }
        return components;
    }
    int index = 0;
    public void generateCombinations(Set<Integer> component, int index ,Set<Integer> combination, Set<Set<Integer>> combinations){
        combinations.add(new HashSet<>(combination)); // Добавляем текущую комбинацию в выходной набор

        for (int vertex : component) {
            if (!combination.contains(vertex)) {
                combination.add(vertex); // Добавляем элемент в текущую комбинацию
                generateCombinations(component, index + 1, combination, combinations); // Рекурсивный вызов
                combination.remove(vertex); // Удаляем последний элемент перед следующим вызовом
            }
        }
    }
}

