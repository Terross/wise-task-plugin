package ru.leti.wise.task.plugin.repository;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.repository.CrudRepository;
import ru.leti.wise.task.plugin.domain.PluginEntity;

import java.util.List;
import java.util.UUID;

@Observed
public interface PluginRepository extends CrudRepository<PluginEntity, UUID> {

    List<PluginEntity> findAll();

}
