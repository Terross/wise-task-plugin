package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.IsOwnerPluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.IsOwnerPluginResponse;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.ErrorCode;
import ru.leti.wise.task.plugin.repository.PluginRepository;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class IsOwnerPluginOperation {

    private final PluginRepository pluginRepository;

    public IsOwnerPluginResponse activate(IsOwnerPluginRequest request) {
        String userId = request.getUserId();
        var pluginEntity = pluginRepository.findById(UUID.fromString(request.getPluginId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.PLUGIN_NOT_FOUND));

        return IsOwnerPluginResponse.newBuilder()
                .setResult(userId.equals(pluginEntity.getAuthorId().toString()))
                .build();
    }
}