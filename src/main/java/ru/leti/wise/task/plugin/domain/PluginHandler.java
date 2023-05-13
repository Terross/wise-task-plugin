package ru.leti.wise.task.plugin.domain;

import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;

public interface PluginHandler {

    String run(Plugin plugin, Solution solution);
}
