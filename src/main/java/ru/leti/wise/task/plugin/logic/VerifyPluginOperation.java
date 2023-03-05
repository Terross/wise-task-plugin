package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.graph.external.ExternalPluginService;
import ru.leti.wise.task.plugin.domain.graph.internal.InternalPluginService;
import ru.leti.wise.task.plugin.model.VerifyPlugin200Response;
import ru.leti.wise.task.plugin.model.VerifyPluginRequest;
import ru.leti.wise.task.plugin.repository.PluginRepository;

import static java.util.UUID.fromString;

@Component
@RequiredArgsConstructor
public class VerifyPluginOperation {

    private final PluginRepository pluginRepository;
    private final InternalPluginService internalPluginService;
    private final ExternalPluginService externalPluginService;

    public VerifyPlugin200Response activate(String id, VerifyPluginRequest verifyPluginRequest) {

        PluginEntity pluginEntity = pluginRepository.findById(fromString(id))
                .orElseThrow(() -> new RuntimeException("Plugin not found")); //TODO: error

        if (pluginEntity.getIsInternal()) {
            return new VerifyPlugin200Response()
                    .result(internalPluginService.run(pluginEntity.getBeanName(),
                            verifyPluginRequest.getPayload().getMainPayload(),
                            verifyPluginRequest.getPayload().getAdditionalData()));
        } else {
           return new VerifyPlugin200Response()
                   .result(externalPluginService.run(pluginEntity.getFileName(),
                           verifyPluginRequest.getPayload().getMainPayload(),
                           verifyPluginRequest.getPayload().getAdditionalData()));
        }
    }
}
