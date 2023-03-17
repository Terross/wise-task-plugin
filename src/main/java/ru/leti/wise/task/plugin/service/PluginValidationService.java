package ru.leti.wise.task.plugin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.graph.external.ExternalPluginService;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.plugin.graph.HandwrittenAnswer;
import ru.leti.wise.task.plugin.graph.NewGraphConstruction;
import ru.leti.wise.task.plugin.service.grpc.GraphGrpcService;

import static java.lang.String.valueOf;

@Component
@RequiredArgsConstructor
public class PluginValidationService {

    @Value("${plugin-validation-limit}")
    private int timeLimit;

    private final GraphGrpcService graphGrpcService;
    private final ExternalPluginService externalPluginService;
    private static final String TEST_HANDWRITTEN_ANSWER = "test";
    public boolean validate(PluginEntity pluginEntity) {
        Graph graph = graphGrpcService.getGraph();

        Thread thread = new Thread(() -> testAbstractPlugin(pluginEntity, graphGrpcService.getGraph()));

        try {
            thread.start();
            Thread.sleep(timeLimit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    //TODO Доавить проверку типов при добавлении новых типов плагинов
    private void testAbstractPlugin(PluginEntity pluginEntity, Graph graph) {
        Plugin plugin = externalPluginService.loadPluginFromJar(pluginEntity.getName());
        externalPluginService.run(pluginEntity.getName(), graph, prepareAdditionData(plugin));
    }

    @SuppressWarnings("unchecked")
    private <T> T prepareAdditionData(Plugin plugin) {
        return switch (plugin) {
            case GraphProperty p -> null;
            case GraphCharacteristic p -> null;
            case HandwrittenAnswer p -> (T) TEST_HANDWRITTEN_ANSWER;
            case NewGraphConstruction p -> (T) graphGrpcService.getGraph();
            default -> throw new IllegalStateException("Unexpected value: " + plugin);
        };
    }
}
