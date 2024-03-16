package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.UpdatePluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.UpdatePluginResponse;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.ErrorCode;
import ru.leti.wise.task.plugin.mapper.PluginMapper;
import ru.leti.wise.task.plugin.repository.PluginRepository;
import ru.leti.wise.task.plugin.service.PluginValidationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePluginOperation {

    private final PluginMapper pluginMapper;
    private final PluginRepository pluginRepository;
    private final PluginValidationService pluginValidationService;

    public UpdatePluginResponse activate(UpdatePluginRequest request) {

        var requestPlugin = pluginMapper.pluginToPluginEntity(request.getPlugin());
        var pluginEntity = pluginRepository.findById(requestPlugin.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PLUGIN_NOT_FOUND));
        pluginMapper.updatePlugin(requestPlugin, pluginEntity);

        if (requestPlugin.getJarFile() != null) {
            pluginEntity.setJarFile(requestPlugin.getJarFile());
            pluginEntity.setJarName(requestPlugin.getJarName());
            if (pluginValidationService.isValidate(requestPlugin)) {
                pluginRepository.save(pluginEntity);
            } else {
                throw new BusinessException(ErrorCode.TOO_LONG_PLUGIN_EXECUTION);
            }
        } else {
            pluginRepository.save(pluginEntity);
        }

        return UpdatePluginResponse.newBuilder()
                .setPlugin(pluginMapper.pluginEntityToPlugin(pluginEntity))
                .build();
    }
}
