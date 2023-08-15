package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginImplementationRequest;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginImplementationResponse;
import ru.leti.wise.task.plugin.PluginOuterClass;
import ru.leti.wise.task.plugin.PluginOuterClass.GraphTestResult;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.PluginType;
import ru.leti.wise.task.plugin.domain.graph.PluginService;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.ErrorCode;
import ru.leti.wise.task.plugin.repository.PluginRepository;
import ru.leti.wise.task.plugin.service.PluginValidationService;
import ru.leti.wise.task.plugin.service.grpc.GraphGrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.UUID.fromString;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;
import static ru.leti.wise.task.plugin.error.ErrorCode.INVALID_PLUGIN_IMPLEMENTATION_TYPE;
import static ru.leti.wise.task.plugin.error.ErrorCode.PLUGIN_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CheckPluginImplementationOperation {

    private final PluginRepository pluginRepository;
    private final GraphGrpcService graphGrpcService;
    private final PluginService externalPluginService;
    private final PluginService internalPluginService;
    private final PluginValidationService pluginValidationService;

    public CheckPluginImplementationResponse activate(CheckPluginImplementationRequest request) {

        var basePluginEntity = pluginRepository.findById(fromString(request.getId()))
                .orElseThrow(() -> new BusinessException(PLUGIN_NOT_FOUND));

        if (basePluginEntity.getPluginType() != PluginType.GRAPH_PROPERTY
                && basePluginEntity.getPluginType() != PluginType.GRAPH_CHARACTERISTIC) {
            throw new BusinessException(INVALID_PLUGIN_IMPLEMENTATION_TYPE);
        }

        List<GraphTestResult> graphTestResults = new ArrayList<>();
        boolean result = true;
        var newPluginEntity = PluginEntity.builder()
                .pluginType(basePluginEntity.getPluginType())
                .jarFile(decodeBase64(request.getFile()))
                .jarName(basePluginEntity.getJarName())
                .build();

        validateImplementation(newPluginEntity);

        for (int i = 0; i < 5; i++) {
            var graphTestResult = runAlgorithms(basePluginEntity, newPluginEntity);
            if (!graphTestResult.getResult().equals(graphTestResult.getOriginalResult())) {
                result = false;
            }
            graphTestResults.add(graphTestResult);

        }

        return CheckPluginImplementationResponse.newBuilder()
                .setImplementationResult(PluginOuterClass.ImplementationResult.newBuilder()
                        .setResult(result)
                        .addAllGraphTaskResult(graphTestResults)
                        .build())
                .build();
    }

    private GraphTestResult runAlgorithms(PluginEntity basePluginEntity, PluginEntity newPluginEntity) {
        var solution = generateSolution();
        final long baseStartTime = System.nanoTime();
        var originalResult = basePluginEntity.getIsInternal()
                ? internalPluginService.run(basePluginEntity, solution)
                : externalPluginService.run(basePluginEntity, solution);
        final long originalDuration = System.nanoTime() - baseStartTime;

        final long newStartTime = System.nanoTime();
        var newResult = internalPluginService.run(newPluginEntity, solution);
        final long newDuration = System.nanoTime() - newStartTime;

        return GraphTestResult.newBuilder()
                .setGraphId(solution.getGraph().getId())
                .setOriginalTimeResult(originalDuration)
                .setTimeResult(newDuration)
                .setResult(newResult)
                .setOriginalResult(originalResult)
                .build();
    }

    private void validateImplementation(PluginEntity newPluginEntity) {
        boolean isValid = pluginValidationService.isValidate(newPluginEntity);
        if (!isValid) {
            throw new BusinessException(ErrorCode.TOO_LONG_PLUGIN_EXECUTION);
        }
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
