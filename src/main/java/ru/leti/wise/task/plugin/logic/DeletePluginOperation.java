package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.ErrorCode;
import ru.leti.wise.task.plugin.repository.PluginRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeletePluginOperation {

    @Value("${path-plugin:./plugins}")
    private String pathPlugin;

    private final PluginRepository pluginRepository;

    public void activate(UUID id) {
        var plugin = pluginRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLUGIN_NOT_FOUND));
        Path path = Paths.get(pathPlugin + plugin.getFileName() + ".jar").normalize().toAbsolutePath();

        deleteFile(path);
        pluginRepository.deleteById(id);
    }

    @SneakyThrows
    private void deleteFile(Path path) {
        Files.delete(path);
    }
}
