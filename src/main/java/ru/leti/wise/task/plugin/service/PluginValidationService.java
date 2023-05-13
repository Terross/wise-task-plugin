package ru.leti.wise.task.plugin.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.domain.graph.external.ExternalPluginService;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.plugin.graph.HandwrittenAnswer;
import ru.leti.wise.task.plugin.graph.NewGraphConstruction;
import ru.leti.wise.task.plugin.service.grpc.GraphGrpcService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

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

    public boolean isValidate(PluginEntity pluginEntity, Path path) {
        var future = executorService.submit(() -> testAbstractPlugin(pluginEntity));
        try {
            future.get(timeLimit, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            deleteInvalidPluginFile(path);
            throw new RuntimeException(e); //TODO понятные ошибки
        } catch (TimeoutException e) {
            deleteInvalidPluginFile(path);
            return false;
        }
        deleteInvalidPluginFile(path);
        return true;
    }

    //TODO Доавить проверку типов при добавлении новых типов плагинов
    private String testAbstractPlugin(PluginEntity pluginEntity) {
        System.out.println(Thread.currentThread().threadId());
        Plugin plugin = externalPluginService.loadPluginFromJar(pluginEntity.getFileName());
        return graphPluginHandler.run(plugin, prepareSolution(plugin));
    }

    @SuppressWarnings("unchecked")
    private Solution prepareSolution(Plugin plugin) {
        var graph = graphGrpcService.getGraph();
        var solutionBuilder = Solution.newBuilder();
        return switch (plugin) {
            case GraphProperty p -> solutionBuilder.setGraph(graph).build();
            case GraphCharacteristic p -> solutionBuilder.setGraph(graph).build();
            case HandwrittenAnswer p -> solutionBuilder.setGraph(graph)
                    .setHandwrittenAnswer(TEST_HANDWRITTEN_ANSWER).build();
            case NewGraphConstruction p -> solutionBuilder.setGraph(graph)
                    .setOtherGraph(graphGrpcService.getGraph()).build();
            default -> throw new IllegalStateException("Unexpected value: " + plugin);
        };
    }

    @SneakyThrows
    private void deleteInvalidPluginFile(Path path) {
        Files.delete(path);
    }
}
