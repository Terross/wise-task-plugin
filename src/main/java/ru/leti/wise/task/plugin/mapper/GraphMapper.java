package ru.leti.wise.task.plugin.mapper;

import org.mapstruct.*;
import ru.leti.GraphOuterClass;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;

@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface GraphMapper {

    Graph graphRequestToGraph(ru.leti.wise.task.plugin.model.Graph graph);
    Vertex vertexRequestToVertex(ru.leti.wise.task.plugin.model.Vertex vertex);
    Edge edgeRequestToEdge(ru.leti.wise.task.plugin.model.Edge edge);
    Color colorRequestToColor(ru.leti.wise.task.plugin.model.Color color);

    Graph toGraph(GraphOuterClass.Graph graph);

    @Mapping(target = "xCoordinate", source = "XCoordinate")
    @Mapping(target = "yCoordinate", source = "YCoordinate")
    Vertex toVertex(GraphOuterClass.Vertex vertex);
    Edge toEdge(GraphOuterClass.Edge edge);

    @ValueMapping(target = MappingConstants.NULL, source = "UNRECOGNIZED")
    @ValueMapping(target = MappingConstants.NULL, source = "GRAY")
    Color toColor(GraphOuterClass.Color color);
}
