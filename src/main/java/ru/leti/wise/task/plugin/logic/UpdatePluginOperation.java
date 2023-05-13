package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.UpdatePluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.UpdatePluginResponse;

@Component
@RequiredArgsConstructor
public class UpdatePluginOperation {

    public UpdatePluginResponse activate(UpdatePluginRequest request) {
        return UpdatePluginResponse.newBuilder().build();
    }
}
