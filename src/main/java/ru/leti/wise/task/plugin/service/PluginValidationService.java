package ru.leti.wise.task.plugin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.GraphOuterClass;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.domain.graph.external.ExternalPluginService;
import ru.leti.wise.task.plugin.error.PluginExecutionException;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.plugin.graph.HandwrittenAnswer;
import ru.leti.wise.task.plugin.graph.NewGraphConstruction;
import ru.leti.wise.task.plugin.service.grpc.GraphGrpcService;

import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PluginValidationService {

    @Value("${plugin-validation-limit}")
    private int timeLimit;

    private final GraphGrpcService graphGrpcService;
    private final ExternalPluginService externalPluginService;
    private final PluginHandler graphPluginHandler;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String TEST_HANDWRITTEN_ANSWER = "test";

    public boolean isValidate(PluginEntity pluginEntity) {
        var future = executorService.submit(() -> testAbstractPlugin(pluginEntity));
        try {
            future.get(timeLimit, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("", e);
            throw new PluginExecutionException(e.getMessage());
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }

    //TODO Доавить проверку типов при добавлении новых типов плагинов
    private String testAbstractPlugin(PluginEntity pluginEntity) {
        Plugin plugin = externalPluginService.loadPluginFromJar(pluginEntity);
        return graphPluginHandler.run(plugin, prepareSolution(plugin));
    }

    @SuppressWarnings("unchecked")
    private Solution prepareSolution(Plugin plugin) {
        var graph = getGraph();
        var solutionBuilder = Solution.newBuilder();
        return switch (plugin) {
            case GraphProperty p -> solutionBuilder.setGraph(graph).build();
            case GraphCharacteristic p -> solutionBuilder.setGraph(graph).build();
            case HandwrittenAnswer p -> solutionBuilder.setGraph(graph)
                    .setHandwrittenAnswer(TEST_HANDWRITTEN_ANSWER).build();
            case NewGraphConstruction p -> solutionBuilder.setGraph(graph)
                    .setOtherGraph(getGraph()).build();
            default -> throw new IllegalStateException("Unexpected value: " + plugin);
        };
    }

    private GraphOuterClass.Graph getGraph() {
        var vertexCount = ThreadLocalRandom.current().nextInt(1, 6);
        var edgeCount = ThreadLocalRandom.current().nextInt(1, 6);
        var isDirect = ThreadLocalRandom.current().nextBoolean();
        return graphGrpcService.getGraph(vertexCount, edgeCount, isDirect);
    }
}
