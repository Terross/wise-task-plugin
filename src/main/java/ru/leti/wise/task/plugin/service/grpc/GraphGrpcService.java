package ru.leti.wise.task.plugin.service.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.GraphOuterClass;
import ru.leti.GraphServiceGrpc.GraphServiceBlockingStub;
import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.plugin.mapper.GraphMapper;

import static io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder.forAddress;
import static ru.leti.GraphServiceGrpc.newBlockingStub;

@Component
@RequiredArgsConstructor
public class GraphGrpcService {

    @Value("${grpc.service.graph.port}")
    private int port;

    private final GraphServiceBlockingStub graphServiceStub
            = newBlockingStub(forAddress("localhost", 6565).usePlaintext().build());

    public GraphOuterClass.Graph getGraph() {
        var request = GraphOuterClass.GenerateGraphRequest.newBuilder()
                .setEdgeCount(5)
                .setVertexCount(3)
                .setIsDirect(true)
                .build();

        return graphServiceStub.generateRandomGraph(request).getGraph();
    }
}
