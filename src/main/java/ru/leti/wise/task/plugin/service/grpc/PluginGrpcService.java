package ru.leti.wise.task.plugin.service.grpc;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import ru.leti.wise.task.plugin.PluginGrpc;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginImplementationRequest;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginSolutionRequest;
import ru.leti.wise.task.plugin.PluginGrpc.CheckPluginSolutionResponse;
import ru.leti.wise.task.plugin.PluginGrpc.DeletePluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.DeletePluginResponse;
import ru.leti.wise.task.plugin.PluginGrpc.UpdatePluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.UpdatePluginResponse;
import ru.leti.wise.task.plugin.PluginGrpc.GetPluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.GetPluginResponse;
import ru.leti.wise.task.plugin.PluginGrpc.CreatePluginRequest;
import ru.leti.wise.task.plugin.PluginGrpc.CreatePluginResponse;
import ru.leti.wise.task.plugin.PluginGrpc.GetAllPluginsResponse;
import ru.leti.wise.task.plugin.PluginServiceGrpc.PluginServiceImplBase;
import ru.leti.wise.task.plugin.logic.*;

import java.util.UUID;

@Slf4j
@GRpcService
@RequiredArgsConstructor
public class PluginGrpcService extends PluginServiceImplBase {

    private final GetPluginsOperation getPluginsOperation;
    private final CheckPluginSolutionOperation checkPluginSolutionOperation;
    private final GetPluginOperation getPluginOperation;
    private final CreateExternalPluginOperation createExternalPluginOperation;
    private final DeletePluginOperation deletePluginOperation;
    private final UpdatePluginOperation updatePluginOperation;

    @Override
    public void getAllPlugins(Empty request, StreamObserver<GetAllPluginsResponse> responseObserver) {
        responseObserver.onNext(getPluginsOperation.activate());
        responseObserver.onCompleted();
    }

    @Override
    public void getPlugin(GetPluginRequest request, StreamObserver<GetPluginResponse> responseObserver) {
        responseObserver.onNext(getPluginOperation.activate(UUID.fromString(request.getId())));
        responseObserver.onCompleted();
    }

    @Override
    public void createPlugin(CreatePluginRequest request, StreamObserver<CreatePluginResponse> responseObserver) {
        responseObserver.onNext(createExternalPluginOperation.activate(request));
        responseObserver.onCompleted();
    }

    @Override
    public void deletePlugin(DeletePluginRequest request, StreamObserver<DeletePluginResponse> responseObserver) {
        deletePluginOperation.activate(UUID.fromString(request.getId()));
        responseObserver.onNext(DeletePluginResponse.newBuilder().setId(request.getId()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updatePlugin(UpdatePluginRequest request, StreamObserver<UpdatePluginResponse> responseObserver) {
        responseObserver.onNext(updatePluginOperation.activate(request));
        responseObserver.onCompleted();
    }

    @Override
    public void checkPluginSolution(CheckPluginSolutionRequest request,
                                    StreamObserver<CheckPluginSolutionResponse> responseObserver) {
        responseObserver.onNext(checkPluginSolutionOperation.activate(request));
        super.checkPluginSolution(request, responseObserver);
    }

    @Override
    public void checkPluginImplementation(CheckPluginImplementationRequest request,
                                          StreamObserver<PluginGrpc.CheckPluginImplementationResponse> responseObserver) {
        super.checkPluginImplementation(request, responseObserver);
    }
}
