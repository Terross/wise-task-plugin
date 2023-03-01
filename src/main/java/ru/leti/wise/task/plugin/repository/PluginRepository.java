package ru.leti.wise.task.plugin.repository;

import org.springframework.data.repository.CrudRepository;
import ru.leti.wise.task.plugin.domain.Plugin;

import java.util.UUID;

public interface PluginRepository extends CrudRepository<Plugin, UUID> {
}
