package ru.leti.wise.task.plugin.error;

import io.grpc.Metadata;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GrpcErrorHandler {
    public Status processBusinessError(BusinessException e, GRpcExceptionScope scope) {
        return switch (e.getErrorCode()) {
            case PROFILE_NOT_FOUND -> Status.NOT_FOUND;
            case INVALID_PASSWORD -> Status.UNAUTHENTICATED;
            default -> Status.UNKNOWN;
        };
    }

    public Status processPluginError(PluginExecutionException e, GRpcExceptionScope scope) {
        scope.getResponseHeaders()
                .put(Metadata.Key.of("error_message", Metadata.ASCII_STRING_MARSHALLER), e.getMessage());
        return Status.INVALID_ARGUMENT;
    }
}
