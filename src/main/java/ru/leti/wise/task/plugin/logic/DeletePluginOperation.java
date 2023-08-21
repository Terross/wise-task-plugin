package ru.leti.wise.task.plugin.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.plugin.repository.PluginRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeletePluginOperation {

    private final PluginRepository pluginRepository;

    public void activate(UUID id) {
        pluginRepository.deleteById(id);
    }

}
