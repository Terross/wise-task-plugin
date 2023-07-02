package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.UpdatePluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.UpdatePluginResponse;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.ErrorCode;
import ru.leti.wise.task.plugin.error.PluginExecutionException;
import ru.leti.wise.task.plugin.mapper.PluginMapper;
import ru.leti.wise.task.plugin.repository.PluginRepository;
import ru.leti.wise.task.plugin.service.PluginValidationService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePluginOperation {

    @Value("${path-plugin:./plugins}")
    private String pathPlugin;

    private final PluginMapper pluginMapper;
    private final PluginRepository pluginRepository;
    private final PluginValidationService pluginValidationService;


    public UpdatePluginResponse activate(UpdatePluginRequest request) {

        var pluginEntity = pluginMapper.pluginToPluginEntity(request.getPlugin());
        var oldPluginEntity = pluginRepository.findById(pluginEntity.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PLUGIN_NOT_FOUND));

        if (!request.getFile().isBlank()) {

            Path oldPath = Paths.get(pathPlugin + oldPluginEntity.getFileName() + ".jar").normalize().toAbsolutePath();
            Path tmpPath = Paths.get(pathPlugin + "/tmp/" + oldPluginEntity.getFileName() + ".jar").normalize().toAbsolutePath();
            Path newPath = Paths.get(pathPlugin + pluginEntity.getFileName() + ".jar").normalize().toAbsolutePath();

            moveFile(oldPath, tmpPath);
            saveFile(request.getFile(), newPath);

            boolean isValid = isValid(pluginEntity, newPath, tmpPath, oldPath);
            if (isValid) {
                pluginRepository.save(pluginEntity);
            } else {
                deleteInvalidPluginFile(newPath);
                moveFile(tmpPath, oldPath);
                throw new BusinessException(ErrorCode.TOO_LONG_PLUGIN_EXECUTION);
            }
            deleteInvalidPluginFile(tmpPath);
        } else {
            pluginRepository.save(pluginMapper.pluginToPluginEntity(request.getPlugin()));
        }


        return UpdatePluginResponse.newBuilder()
                .setPlugin(request.getPlugin())
                .build();
    }

    private static void moveFile(Path source, Path target) {
        try {
            Files.move(source, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFile(String base64Data, Path path) {
        try (InputStream inputStream = new ByteArrayInputStream(decodeBase64(base64Data))) {
            Files.copy(inputStream, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValid(PluginEntity pluginEntity, Path newPath, Path tmpPath, Path oldPath) {
        try {
            return pluginValidationService.isValidate(pluginEntity);
        } catch (ExecutionException | InterruptedException e) {
            log.error("", e);
            deleteInvalidPluginFile(newPath);
            moveFile(tmpPath, oldPath);
            throw new PluginExecutionException(e.getMessage());
        }
    }

    @SneakyThrows
    private void deleteInvalidPluginFile(Path path) {
        Files.delete(path);
    }
}
