package ru.leti.wise.task.plugin.domain.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.plugin.mapper.GraphMapper;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalPluginService {

    private final GraphMapper graphMapper;


    public String run(String fileName, ru.leti.wise.task.plugin.model.RequestPayloadMainPayload mainPayload, ru.leti.wise.task.plugin.model.RequestPayloadAdditionalData additionalData) {

        try {
            File jarFile = new File("./plugins/EvenVerticesNumber.jar");
            DynamicClassLoader classLoader = new DynamicClassLoader(new URLClassLoader(
                    new URL[]{jarFile.toURI().toURL()},
                    this.getClass().getClassLoader()
            ));
            Class<?> pluginClass = null;
            pluginClass = classLoader.loadClass("EvenVerticesNumber");

            assert pluginClass != null;
            Plugin instance = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            return switch (instance) {
                case GraphCharacteristic characteristic -> String.valueOf(characteristic.run(graphMapper.graphRequestToGraph((ru.leti.wise.task.plugin.model.Graph) mainPayload)));
                case GraphProperty property -> property.run(graphMapper.graphRequestToGraph((ru.leti.wise.task.plugin.model.Graph) mainPayload)) ? "верно": "неверно";
                default -> "Ошибка надо обработать";
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
