package ru.leti.wise.task.plugin.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.api.PluginApiDelegate;
import ru.leti.wise.task.plugin.logic.*;
import ru.leti.wise.task.plugin.model.CreateExternalPluginRequest;
import ru.leti.wise.task.plugin.model.Plugin;
import ru.leti.wise.task.plugin.model.VerifyPlugin200Response;
import ru.leti.wise.task.plugin.model.VerifyPluginRequest;
import ru.leti.wise.task.plugin.model.VerifyCustomPluginImplementationRequest;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PluginController implements PluginApiDelegate {

    private final GetPluginsOperation getPluginsOperation;
    private final VerifyPluginOperation verifyPluginOperation;
    private final GetPluginOperation getPluginOperation;
    private final CreateExternalPluginOperation createExternalPluginOperation;
    private final DeletePluginOperation deletePluginOperation;

    @Override
    public ResponseEntity<VerifyPlugin200Response>
    verifyCustomPluginImplementation(String id,
                                     VerifyCustomPluginImplementationRequest verifyCustomPluginImplementationRequest) {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Plugin> createExternalPlugin(CreateExternalPluginRequest createExternalPluginRequest) {
        return ResponseEntity.ok(createExternalPluginOperation.activate(createExternalPluginRequest));
    }

    @Override
    public ResponseEntity<Void> deletePlugin(String id) {
        deletePluginOperation.activate(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Plugin> getPlugin(String id) {
        return ResponseEntity.ok(getPluginOperation.activate(UUID.fromString(id)));
    }

    @Override
    public ResponseEntity<List<Plugin>> getPlugins() {
        return ResponseEntity.ok(getPluginsOperation.activate());
    }

    @Override
    public ResponseEntity<VerifyPlugin200Response> verifyPlugin(String id, VerifyPluginRequest verifyPluginRequest) {
        return ResponseEntity.ok(verifyPluginOperation.activate(id, verifyPluginRequest));
    }

    @Override
    public ResponseEntity<Plugin> updatePlugin(String id, ru.leti.wise.task.plugin.model.UpdatePluginRequest updatePluginRequest) {
        return PluginApiDelegate.super.updatePlugin(id, updatePluginRequest);
    }

}
