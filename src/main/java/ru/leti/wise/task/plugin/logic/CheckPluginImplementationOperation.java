package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginImplementationResponse;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginImplementationRequest;

@Component
@RequiredArgsConstructor
public class CheckPluginImplementationOperation {

    public CheckPluginImplementationResponse activate(CheckPluginImplementationRequest request) {
        return CheckPluginImplementationResponse.newBuilder()
                .build();
    }
}
