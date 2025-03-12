/*
 * Проверка, является ли граф 2-критическим.
 *  */

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.graph.GraphProperty;

@Component
public class IsTwoCritical implements GraphProperty {
    @Override
    public boolean run(Graph graph) {
        return graph.getVertexCount() == 2 && graph.getEdgeCount() == 1;
    }
}

