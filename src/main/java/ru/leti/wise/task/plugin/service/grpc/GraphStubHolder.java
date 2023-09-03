package ru.leti.wise.task.plugin.service.grpc;

import io.grpc.ClientInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.GraphServiceGrpc;

import javax.annotation.PostConstruct;

import static io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder.forAddress;
import static ru.leti.wise.task.graph.GraphServiceGrpc.newBlockingStub;

@Component
@RequiredArgsConstructor
public class GraphStubHolder {

    @Value("${grpc.service.graph.port}")
    private int port;
    @Value("${grpc.service.graph.host}")
    private String host;

    private GraphServiceGrpc.GraphServiceBlockingStub graphServiceStub;
    private final ClientInterceptor grpcTracingClientInterceptor;

    @PostConstruct
    void init() {
        graphServiceStub = newBlockingStub(forAddress(host, port)
                .intercept(grpcTracingClientInterceptor)
                .usePlaintext().build());
    }

    GraphServiceGrpc.GraphServiceBlockingStub get() {
        return graphServiceStub;
    }
}
