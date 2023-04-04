package ru.leti.wise.task.plugin.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.leti.wise.task.plugin.domain.PluginEntity;

import java.util.List;
import java.util.UUID;

public interface PluginRepository extends CrudRepository<PluginEntity, UUID> {

    List<PluginEntity> findAll();

}
