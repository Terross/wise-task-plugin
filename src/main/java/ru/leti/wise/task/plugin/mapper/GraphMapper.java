package ru.leti.wise.task.plugin.mapper;

import org.mapstruct.*;
import ru.leti.wise.task.graph.GraphOuterClass;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;

@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface GraphMapper {

    Graph toGraph(GraphOuterClass.Graph graph);

    @Mapping(target = "xCoordinate", source = "XCoordinate")
    @Mapping(target = "yCoordinate", source = "YCoordinate")
    Vertex toVertex(GraphOuterClass.Vertex vertex);
    Edge toEdge(GraphOuterClass.Edge edge);

    @ValueMapping(target = MappingConstants.NULL, source = "UNRECOGNIZED")
    @ValueMapping(target = MappingConstants.NULL, source = "GRAY")
    Color toColor(GraphOuterClass.Color color);
}
