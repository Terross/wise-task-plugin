package ru.leti.wise.task.plugin.domain.graph.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.graph.GraphPlugin;

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

    public <T, P> String run(String fileName, Solution solution) {

        return switch (loadPluginFromJar(fileName)) {
            case GraphPlugin p -> graphPluginHandler.run(p, solution);
            default -> throw new IllegalStateException("Unexpected value: " + fileName);
        };
    }

    public Plugin loadPluginFromJar(String fileName) {
        try {
            File jarFile = new File("%s/%s.jar".formatted(basePath, fileName));
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, this.getClass().getClassLoader());
            Class<?> pluginClass = Class.forName(fileName, true, classLoader);
            return (Plugin) pluginClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
