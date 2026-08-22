package ru.leti.wise.task.plugin.error;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GrpcErrorHandler {
    public Status processBusinessError(BusinessException e) {
        return switch (e.getErrorCode()) {
            case PROFILE_NOT_FOUND -> Status.NOT_FOUND;
            case INVALID_PASSWORD -> Status.UNAUTHENTICATED;
            default -> Status.UNKNOWN;
        };
    }

    public StatusRuntimeException processPluginError(PluginExecutionException e) {
        var metadata = new Metadata();
        Metadata.Key<String> errorMessageKey = Metadata.Key.of("error_message", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(errorMessageKey, e.getMessage());
        return Status.INVALID_ARGUMENT
                .withDescription("Plugin execution failed: " + e.getMessage())
                .asRuntimeException(metadata);
    }
}
