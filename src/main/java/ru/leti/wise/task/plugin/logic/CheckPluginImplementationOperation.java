package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginImplementationResponse;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginImplementationRequest;
import ru.leti.wise.task.plugin.PluginOuterClass;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.PluginOuterClass.GraphTestResult;
import ru.leti.wise.task.plugin.domain.graph.PluginService;
import ru.leti.wise.task.plugin.domain.graph.external.ExternalPluginService;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.ErrorCode;
import ru.leti.wise.task.plugin.repository.PluginRepository;
import ru.leti.wise.task.plugin.service.grpc.GraphGrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.UUID.fromString;

@Component
@RequiredArgsConstructor
public class CheckPluginImplementationOperation {

    private final PluginRepository pluginRepository;
    private final GraphGrpcService graphGrpcService;
    private final PluginService externalPluginService;
    private final PluginService internalPluginService;

    public CheckPluginImplementationResponse activate(CheckPluginImplementationRequest request) {

        var pluginEntity = pluginRepository.findById(fromString(request.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.PLUGIN_NOT_FOUND));
        List<GraphTestResult> graphTestResults = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            var solution = generateSolution();

        }


        return CheckPluginImplementationResponse.newBuilder()
                .build();
    }

    private Solution generateSolution() {
        var vertexCount = ThreadLocalRandom.current().nextInt(1, 6);
        var edgeCount = ThreadLocalRandom.current().nextInt(1, 6);
        var isDirect = ThreadLocalRandom.current().nextBoolean();
        var graph = graphGrpcService.getGraph(vertexCount, edgeCount, isDirect);
        return PluginOuterClass.Solution.newBuilder()
                .setGraph(graph)
                .build();
    }
}
