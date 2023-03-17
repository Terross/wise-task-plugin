package ru.leti.wise.task.plugin.domain;

import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.Plugin;

public interface PluginHandler {

    <T> String run(Plugin plugin, Graph graph, T additionalData);
}
