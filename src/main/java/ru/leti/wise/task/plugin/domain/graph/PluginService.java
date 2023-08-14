package ru.leti.wise.task.plugin.domain.graph;

import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginEntity;

public interface PluginService {

    String run(PluginEntity plugin, Solution solution);
}
