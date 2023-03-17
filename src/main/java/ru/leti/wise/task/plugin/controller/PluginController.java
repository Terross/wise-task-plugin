package ru.leti.wise.task.plugin.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.api.PluginApiDelegate;
import ru.leti.wise.task.plugin.logic.CreateExternalPluginOperation;
import ru.leti.wise.task.plugin.logic.GetPluginOperation;
import ru.leti.wise.task.plugin.logic.GetPluginsOperation;
import ru.leti.wise.task.plugin.logic.VerifyPluginOperation;
import ru.leti.wise.task.plugin.model.CreateExternalPluginRequest;
import ru.leti.wise.task.plugin.model.VerifyPlugin200Response;
import ru.leti.wise.task.plugin.model.VerifyPluginRequest;
import ru.leti.wise.task.plugin.model.Plugin;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PluginController implements PluginApiDelegate {

    private final GetPluginsOperation getPluginsOperation;
    private final VerifyPluginOperation verifyPluginOperation;
    private final GetPluginOperation getPluginOperation;
    private final CreateExternalPluginOperation createExternalPluginOperation;

    @Override
    public ResponseEntity<Plugin> createExternalPlugin(CreateExternalPluginRequest createExternalPluginRequest) {
        createExternalPluginOperation
                .activate(createExternalPluginRequest.getPluginInfo(), createExternalPluginRequest.getPluginFile());
        return ResponseEntity.ok(createExternalPluginRequest.getPluginInfo());
    }

    @Override
    public ResponseEntity<Void> deletePlugin(String id) {
        return PluginApiDelegate.super.deletePlugin(id);
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
    public ResponseEntity<Plugin> updatePlugin(String id, CreateExternalPluginRequest createExternalPluginRequest) {
        return PluginApiDelegate.super.updatePlugin(id, createExternalPluginRequest);
    }

    @Override
    public ResponseEntity<VerifyPlugin200Response> verifyPlugin(String id, VerifyPluginRequest verifyPluginRequest) {
        return ResponseEntity.ok(verifyPluginOperation.activate(id, verifyPluginRequest));
    }
}
