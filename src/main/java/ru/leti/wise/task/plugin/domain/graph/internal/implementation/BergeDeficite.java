/*
* Этот код реализует вычисление дефицита Берже для графа.
*
* Дефицит Берже показывает, сколько вершин остаётся "непокрытыми" в графе,
* если выделить максимально возможное количество непересекающихся рёбер.
*/

package ru.leti.wise.task.plugin.domain.graph.internal.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

@Component
public class BergeDeficite implements GraphCharacteristic {

    private final MaximalMatching maximalMatching;

    // Внедрение зависимости через конструктор (рекомендуемый способ)
    @Autowired
    public BergeDeficite(MaximalMatching maximalMatching) {
        this.maximalMatching = maximalMatching;
    }

    @Override
    public int run(Graph graph) {
        return graph.getVertexCount() - 2 * maximalMatching.run(graph);
    }
}
