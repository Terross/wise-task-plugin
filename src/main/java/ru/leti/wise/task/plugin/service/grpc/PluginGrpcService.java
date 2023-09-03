package ru.leti.wise.task.plugin.service.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;
import ru.leti.wise.task.plugin.PluginGrpc;
import ru.leti.wise.task.plugin.PluginGrpc.*;
import ru.leti.wise.task.plugin.PluginServiceGrpc.PluginServiceImplBase;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.error.GrpcErrorHandler;
import ru.leti.wise.task.plugin.error.PluginExecutionException;
import ru.leti.wise.task.plugin.helper.LogInterceptor;
import ru.leti.wise.task.plugin.logic.*;

import java.util.UUID;

@Slf4j
@Observed
@GRpcService(interceptors = { LogInterceptor.class })
@RequiredArgsConstructor
public class PluginGrpcService extends PluginServiceImplBase {

    private final GetPluginOperation getPluginOperation;
    private final GetPluginsOperation getPluginsOperation;
    private final DeletePluginOperation deletePluginOperation;
    private final UpdatePluginOperation updatePluginOperation;
    private final CheckPluginSolutionOperation checkPluginSolutionOperation;
    private final CreateExternalPluginOperation createExternalPluginOperation;
    private final CheckPluginImplementationOperation checkPluginImplementationOperation;

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
        responseObserver.onCompleted();
    }

    @Override
    public void checkPluginImplementation(CheckPluginImplementationRequest request,
                                          StreamObserver<PluginGrpc.CheckPluginImplementationResponse> responseObserver) {
        responseObserver.onNext(checkPluginImplementationOperation.activate(request));
        responseObserver.onCompleted();
    }

    @GRpcServiceAdvice
    @RequiredArgsConstructor
    static class ErrorHandler {
        private final GrpcErrorHandler grpcErrorHandler;

        @GRpcExceptionHandler
        public Status handleBusinessException(BusinessException e, GRpcExceptionScope scope) {
            return grpcErrorHandler.processBusinessError(e, scope);
        }

        @GRpcExceptionHandler
        public Status handlePluginExecutionException(PluginExecutionException e, GRpcExceptionScope scope) {
            return grpcErrorHandler.processPluginError(e, scope);
        }
    }
}
