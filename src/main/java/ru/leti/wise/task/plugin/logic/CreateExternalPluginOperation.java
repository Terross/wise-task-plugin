package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.mapper.PluginMapper;
import ru.leti.wise.task.plugin.model.Plugin;
import ru.leti.wise.task.plugin.repository.PluginRepository;
import ru.leti.wise.task.plugin.service.PluginValidationService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Transactional
@RequiredArgsConstructor
public class CreateExternalPluginOperation {
    
    @Value("${path-plugin:./plugins}")
    private String pathPlugin;

    private final PluginMapper pluginMapper;
    private final PluginRepository pluginRepository;
    private final PluginValidationService pluginValidationService;

    public void activate(Plugin plugin, Resource resource) {
        if (resource.isFile()) {
            PluginEntity pluginEntity = pluginMapper.pluginToPluginEntity(plugin);
            Path path = Paths.get(pathPlugin + pluginEntity.getFileName()).normalize().toAbsolutePath();
            saveFile(resource, path);

            pluginValidationService.validate(pluginEntity);
            pluginRepository.save(pluginEntity);
        }
    }

    private void saveFile(Resource resource, Path path) {
        try (InputStream inputStream = resource.getInputStream()){
            Files.copy(inputStream, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
