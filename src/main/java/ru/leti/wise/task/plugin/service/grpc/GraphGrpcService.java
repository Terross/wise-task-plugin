package ru.leti.wise.task.plugin.service.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.GraphOuterClass;

@Component
@RequiredArgsConstructor
public class GraphGrpcService {

    private final GraphStubHolder graphStubHolder;

    public GraphOuterClass.Graph getGraph() {
        var request = GraphOuterClass.GenerateGraphRequest.newBuilder()
                .setEdgeCount(5)
                .setVertexCount(3)
                .setIsDirect(true)
                .build();

        return graphStubHolder.get().generateRandomGraph(request).getGraph();
    }
}
