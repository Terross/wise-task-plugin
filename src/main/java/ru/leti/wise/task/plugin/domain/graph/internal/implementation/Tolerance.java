package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.*;
import ru.leti.wise.task.plugin.graph.*;

import java.util.*;

@Component
public class Tolerance implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        // Проверка симметрии:
        if (graph.isDirect()) { // Для направленных графов
            if (graph.getEdgeCount() % 2 != 0) {
                return false;
            }

            int flag = 0;
            for (int i = 0; i < graph.getEdgeCount(); i++) {
                Edge edgeTmpFirst = graph.getEdgeList().get(i);
                for (int j = i + 1; j < graph.getEdgeCount(); j++) {
                    Edge edgeTmpSecond = graph.getEdgeList().get(j);
                    // Сравниваем вершины рёбер
                    if (edgeTmpFirst.getTarget() == edgeTmpSecond.getSource() &&
                            edgeTmpSecond.getTarget() == edgeTmpFirst.getSource()) {
                        flag += 1;
                    }
                }
            }
            return flag == graph.getEdgeCount() / 2;
        } else {
            // Для неориентированных графов симметрия всегда выполняется
            return true;
        }
    }
}
