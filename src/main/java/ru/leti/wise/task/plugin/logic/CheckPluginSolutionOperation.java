package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginSolutionRequest;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginSolutionResponse;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.graph.external.ExternalPluginService;
import ru.leti.wise.task.plugin.domain.graph.internal.InternalPluginService;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.ErrorCode;
import ru.leti.wise.task.plugin.repository.PluginRepository;

import static java.util.UUID.fromString;

@Component
@RequiredArgsConstructor
public class CheckPluginSolutionOperation {

    private final PluginRepository pluginRepository;
    private final InternalPluginService internalPluginService;
    private final ExternalPluginService externalPluginService;

    public CheckPluginSolutionResponse activate(CheckPluginSolutionRequest request) {

        var solution = request.getSolution();
        PluginEntity pluginEntity = pluginRepository.findById(fromString(solution.getPluginId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.PLUGIN_NOT_FOUND));

        return pluginEntity.getIsInternal()
                ? buildInternalPluginResponse(pluginEntity, solution)
                : buildExternalPluginResponse(pluginEntity, solution);
    }

    private CheckPluginSolutionResponse buildInternalPluginResponse(PluginEntity pluginEntity, Solution solution) {
        var result = internalPluginService.run(pluginEntity.getBeanName(), solution);
        return CheckPluginSolutionResponse.newBuilder()
                .setResult(result)
                .build();
    }

    private CheckPluginSolutionResponse buildExternalPluginResponse(PluginEntity pluginEntity, Solution solution) {
        var result = externalPluginService.run(pluginEntity.getFileName(), solution);
        return CheckPluginSolutionResponse.newBuilder()
                .setResult(result)
                .build();
    }
}
