package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.graph.external.ExternalPluginService;
import ru.leti.wise.task.plugin.domain.graph.internal.InternalPluginService;
import ru.leti.wise.task.plugin.mapper.GraphMapper;
import ru.leti.wise.task.plugin.model.Graph;
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
    private final GraphMapper graphMapper;

    public VerifyPlugin200Response activate(String id, VerifyPluginRequest verifyPluginRequest) {

        PluginEntity pluginEntity = pluginRepository.findById(fromString(id))
                .orElseThrow(() -> new RuntimeException("Plugin not found")); //TODO: error

        return pluginEntity.getIsInternal()
                ? buildInternalPluginResponse(pluginEntity, verifyPluginRequest)
                : buildExternalPluginResponse(pluginEntity, verifyPluginRequest);
    }

    private VerifyPlugin200Response buildInternalPluginResponse(PluginEntity pluginEntity, VerifyPluginRequest request) {
        return new VerifyPlugin200Response()
                .result(internalPluginService.run(pluginEntity.getBeanName(),
                        graphMapper.graphRequestToGraph((Graph) request.getPayload().getMainPayload()),
                        request.getPayload().getAdditionalData()));
    }

    private VerifyPlugin200Response buildExternalPluginResponse(PluginEntity pluginEntity, VerifyPluginRequest request) {
        return new VerifyPlugin200Response()
                .result(externalPluginService.run(pluginEntity.getBeanName(),
                        graphMapper.graphRequestToGraph((Graph) request.getPayload().getMainPayload()),
                        request.getPayload().getAdditionalData()));
    }
}
