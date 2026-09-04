package ru.leti.wise.task.plugin.helper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.graph.util.JsonUtils;
import ru.leti.wise.task.plugin.PluginOuterClass;
import ru.leti.wise.task.plugin.configuration.props.DockerProperties;
import ru.leti.wise.task.plugin.domain.PluginEntity;
import ru.leti.wise.task.plugin.error.BusinessException;
import ru.leti.wise.task.plugin.mapper.GraphMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JarExecutor {
    private final DockerProperties properties;
    private final DockerClient dockerClient;
    private final GraphMapper graphMapper;
    @Value("${spring.plugin.temp-folder}")
    private String tempFolder;
    String containerBaseDir = "/app";

    @SneakyThrows
    public String executeJar(PluginEntity plugin, PluginOuterClass.Solution solution) {
        if (solution.getPayloadCase() != PluginOuterClass.Solution.PayloadCase.GRAPH) {
            throw new BusinessException(Status.INVALID_ARGUMENT, "Плагин не поддерживает графы");
        }
        var graph = graphMapper.toGraph(solution.getGraph());
        var randomId = UUID.randomUUID();
        var jarFileName = plugin.getJarName() + "-" + randomId + ".jar";
        var resultFileName = "result-" + randomId + ".txt";
        var runnerFileName = "runner-" + randomId + ".java";
        var runnerJarFileName = "runner-" + randomId + ".jar";
        var jsonGraphFileName = "graph-" + randomId + ".json";
        Path pluginPath = Paths.get(tempFolder, jarFileName);
        Path resultPath = Paths.get(tempFolder, resultFileName);
        Path runnerPath = Paths.get(tempFolder, runnerFileName);
        Path runnerJarPath = Paths.get(tempFolder, runnerJarFileName);
        Path graphPath = Paths.get(tempFolder, jsonGraphFileName);
        Files.write(pluginPath, plugin.getJarFile());
        var runner = createRunner(graphPath.toString(), pluginPath.toString(), resultPath.toString());
        Files.writeString(runnerPath, runner);
        var jsonGraph = JsonUtils.serializeGraph(graph);
        Files.writeString(graphPath, jsonGraph);
        var bind = new Bind(tempFolder, new Volume(containerBaseDir));
        var container = dockerClient.createContainerCmd("eclipse-temurin:25-jvm-alpine")
                .withCmd("sh -c javac -cp",
                        runnerPath.toString(),
                        runnerJarPath.toString(),
                        "&& java -cp", runnerJarPath + ":" + containerBaseDir, "Runner"
                )
                .withHostConfig(HostConfig.newHostConfig()
                        .withMemory(properties.memoryUsageMb() * 1024 * 1024)
                        .withCpuQuota(properties.cpuQuota())
                        .withReadonlyRootfs(true)
                        .withNetworkMode("none")
                        .withBinds(bind)
                ).exec();
        dockerClient.stopContainerCmd(container.getId())
                .wait(properties
                        .containerWorkTimeout()
                        .toMillis()
                );

    }

    private String createRunner(
            String graphPath,
            String pluginPath,
            String resultPath
    ) {
        return """
                import java.nio.file.Files;
                import java.nio.file.Paths;
                
                public class Runner {
                    public static void main(String[] args) throws Exception {
                        String graphJson = Files.readString(Paths.get("%s"));
                        Graph graph = new ObjectMapper().readValue(graphJson, Graph.class);
                
                        Plugin plugin = (Plugin) Class.forName("%s")
                            .getDeclaredConstructor()
                            .newInstance();
                        String result = String.valueOf(plugin.run(graph));
                        Files.writeString(Paths.get("%s"), result);
                    }
                }
                """.formatted(graphPath, pluginPath, resultPath);
    }
}
