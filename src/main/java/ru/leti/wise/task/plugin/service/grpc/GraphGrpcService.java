package ru.leti.wise.task.plugin.service.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.GraphGrpc.GenerateGraphRequest;
import ru.leti.wise.task.graph.GraphOuterClass.Graph;


@Component
@RequiredArgsConstructor
public class GraphGrpcService {

    private final GraphStubHolder graphStubHolder;

    public Graph getGraph() {
        var request = GenerateGraphRequest.newBuilder()
                .setEdgeCount(5)
                .setVertexCount(3)
                .setIsDirect(true)
                .build();

        return graphStubHolder.get().generateRandomGraph(request).getGraph();
    }
}
