package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.leti.wise.task.plugin.PluginGrpc.CreatePluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.CreatePluginResponse;
import ru.leti.wise.task.plugin.PluginOuterClass.Plugin;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.domain.PluginType;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.ErrorCode;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;
import ru.leti.wise.task.plugin.graph.GraphProperty;
import ru.leti.wise.task.plugin.graph.HandwrittenAnswer;
import ru.leti.wise.task.plugin.graph.NewGraphConstruction;
import ru.leti.wise.task.plugin.mapper.PluginMapper;
import ru.leti.wise.task.plugin.repository.PluginRepository;
import ru.leti.wise.task.plugin.service.PluginValidationService;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class CreateExternalPluginOperation {

    private final PluginMapper pluginMapper;
    private final PluginRepository pluginRepository;
    private final PluginValidationService pluginValidationService;

    public CreatePluginResponse activate(CreatePluginRequest request) {

        Plugin plugin = request.getPlugin();

        PluginEntity pluginEntity = pluginMapper.pluginToPluginEntity(plugin);
        boolean isValid = pluginValidationService.isValidate(pluginEntity);
        if (isValid) {
            pluginRepository.save(pluginEntity);
        } else {
            throw new BusinessException(ErrorCode.TOO_LONG_PLUGIN_EXECUTION);
        }
        return CreatePluginResponse.newBuilder()
                .setPlugin(plugin)
                .build();
    }
}
