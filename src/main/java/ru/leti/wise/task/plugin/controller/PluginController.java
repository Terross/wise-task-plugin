package ru.leti.wise.task.plugin.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.api.PluginApiDelegate;
import ru.leti.wise.task.plugin.model.CreateExternalPluginRequest;
import ru.leti.wise.task.plugin.model.Plugin;
import ru.leti.wise.task.plugin.model.VerifyPlugin200Response;
import ru.leti.wise.task.plugin.model.VerifyPluginRequest;

import java.util.List;

@Component
public class PluginController implements PluginApiDelegate {

    @Override
    public ResponseEntity<Plugin> createExternalPlugin(CreateExternalPluginRequest createExternalPluginRequest) {
        return PluginApiDelegate.super.createExternalPlugin(createExternalPluginRequest);
    }

    @Override
    public ResponseEntity<Void> deletePlugin(String id) {
        return PluginApiDelegate.super.deletePlugin(id);
    }

    @Override
    public ResponseEntity<Plugin> getPlugin(String id) {
        return PluginApiDelegate.super.getPlugin(id);
    }

    @Override
    public ResponseEntity<List<Plugin>> getPlugins() {
        return PluginApiDelegate.super.getPlugins();
    }

    @Override
    public ResponseEntity<Plugin> updatePlugin(String id, CreateExternalPluginRequest createExternalPluginRequest) {
        return PluginApiDelegate.super.updatePlugin(id, createExternalPluginRequest);
    }

    @Override
    public ResponseEntity<VerifyPlugin200Response> verifyPlugin(String id, VerifyPluginRequest verifyPluginRequest) {
        return PluginApiDelegate.super.verifyPlugin(id, verifyPluginRequest);
    }
}
