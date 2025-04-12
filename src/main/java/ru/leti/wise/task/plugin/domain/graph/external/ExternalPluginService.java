package ru.leti.wise.task.plugin.domain.graph.external;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.Plugin;
import ru.leti.wise.task.plugin.PluginOuterClass.Solution;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.PluginHandler;
import ru.leti.wise.task.plugin.domain.graph.PluginService;
import ru.leti.wise.task.plugin.graph.GraphPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

import static java.time.LocalTime.now;
import static java.util.UUID.randomUUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalPluginService implements PluginService {

    private final PluginHandler graphPluginHandler;

    @Value("${path-plugin}")
    private String basePath;

    public String run(PluginEntity plugin, Solution solution) {

        if (loadPluginFromJar(plugin) instanceof GraphPlugin p) {
            return graphPluginHandler.run(p, solution);
        } else {
            throw new IllegalStateException("Unexpected value in plugin " + plugin.getId());
        }

    }

    public Plugin loadPluginFromJar(PluginEntity plugin) {
        makeBaseDir();
        File outputFile = new File("%s/%s.jar".formatted(basePath, plugin.getJarName() + randomUUID() + now()));
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(plugin.getJarFile());
            URLClassLoader classLoader = new URLClassLoader(new URL[]{outputFile.toURI().toURL()}, this.getClass().getClassLoader());
            Class<?> pluginClass = Class.forName(plugin.getJarName(), true, classLoader);
            return (Plugin) pluginClass.getDeclaredConstructor().newInstance();
        } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } finally {
            deleteTemporaryFile(outputFile);
        }
    }

    private void makeBaseDir() {
        File baseDir = new File(basePath);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    @SneakyThrows
    private void deleteTemporaryFile(File file) {
        Files.deleteIfExists(file.toPath());
    }
}
