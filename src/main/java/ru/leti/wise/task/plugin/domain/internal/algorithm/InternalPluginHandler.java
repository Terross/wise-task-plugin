package ru.leti.wise.task.plugin.domain.internal.algorithm;

import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.model.RequestPayloadAdditionalData;
import java.util.List;

public interface InternalPluginHandler {

    String run(Plugin plugin, Graph graph, RequestPayloadAdditionalData additionalData);
}
