package ru.leti.wise.task.plugin.domain;

import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.model.RequestPayloadAdditionalData;
import ru.leti.wise.task.plugin.model.RequestPayloadMainPayload;

public interface PluginHandler {

    String run(Plugin plugin, RequestPayloadMainPayload mainPayload, RequestPayloadAdditionalData additionalData);
}
