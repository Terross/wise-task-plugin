package ru.leti.wise.task.plugin.domain.graph.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.graph.GraphPlugin;
import ru.leti.wise.task.plugin.model.RequestPayloadAdditionalData;
import ru.leti.wise.task.plugin.model.RequestPayloadMainPayload;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalPluginService {

    private final PluginHandler graphPluginHandler;

    @Value("${path-plugin}")
    private String basePath;

    public String run(String className, RequestPayloadMainPayload mainPayload, RequestPayloadAdditionalData additionalData) {

        return switch (loadPluginFromJar(className)) {
            case GraphPlugin p -> graphPluginHandler.run(p, mainPayload, additionalData);
            default -> throw new IllegalStateException("Unexpected value: " + className);
        };
    }

    public Plugin loadPluginFromJar(String className) {
        try {
            File jarFile = new File("%s/%s.jar".formatted(basePath, className));
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, this.getClass().getClassLoader());
            Class<?> pluginClass = Class.forName(className, true, classLoader);
            return (Plugin) pluginClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
