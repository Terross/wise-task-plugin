package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.leti.wise.task.plugin.PluginGrpc;
import ru.leti.wise.task.plugin.PluginGrpc.CreatePluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.CreatePluginResponse;
import ru.leti.wise.task.plugin.PluginOuterClass;
import ru.leti.wise.task.plugin.PluginOuterClass.Plugin;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.mapper.PluginMapper;
import ru.leti.wise.task.plugin.repository.PluginRepository;
import ru.leti.wise.task.plugin.service.PluginValidationService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;

@Component
@Transactional
@RequiredArgsConstructor
public class CreateExternalPluginOperation {
    
    @Value("${path-plugin:./plugins}")
    private String pathPlugin;

    private final PluginMapper pluginMapper;
    private final PluginRepository pluginRepository;
    private final PluginValidationService pluginValidationService;

    public CreatePluginResponse activate(CreatePluginRequest request) {

        Plugin plugin = request.getPlugin();

        PluginEntity pluginEntity = pluginMapper.pluginToPluginEntity(plugin);

        Path path = Paths.get(pathPlugin + pluginEntity.getFileName() + ".jar").normalize().toAbsolutePath();

        saveFile(request.getFile(), path);
        if (pluginValidationService.isValidate(pluginEntity, path)) {
            pluginRepository.save(pluginEntity);
        } else {
            throw new RuntimeException("Не хватило времени");//TODO понятные ошибки
        }
        return CreatePluginResponse.newBuilder()
                .setPlugin(plugin)
                .build();
    }

    private void saveFile(String base64Data, Path path) {
        try (InputStream inputStream = new ByteArrayInputStream(decodeBase64(base64Data))) {
            Files.copy(inputStream, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
