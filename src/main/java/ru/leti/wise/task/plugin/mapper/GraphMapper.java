package ru.leti.wise.task.plugin.mapper;

import org.mapstruct.Mapper;
import ru.leti.wise.task.graph.model.Color;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;

@Mapper(componentModel = "spring")
public interface GraphMapper {

    Graph graphRequestToGraph(ru.leti.wise.task.plugin.model.Graph graph);
    Vertex vertexRequestToVertex(ru.leti.wise.task.plugin.model.Vertex vertex);
    Edge edgeRequestToEdge(ru.leti.wise.task.plugin.model.Edge edge);

    Color colorRequestToColor(ru.leti.wise.task.plugin.model.Color color);
}
