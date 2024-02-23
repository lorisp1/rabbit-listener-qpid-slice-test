package org.example;

import lombok.SneakyThrows;
import org.apache.qpid.server.SystemLauncher;
import org.apache.qpid.server.model.SystemConfig;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QpidBroker {

    private final SystemLauncher systemLauncher = new SystemLauncher();

    @SneakyThrows
    public void startup() {
        systemLauncher.startup(createSystemConfig());
    }

    public void shutdown() {
        systemLauncher.shutdown();
    }

    @SneakyThrows
    private static Map<String, Object> createSystemConfig() {
        Map<String, Object> attributes = new HashMap<>();
        URL initialConfigUrl = QpidBroker.class.getClassLoader().getResource("qpid-config.json");
        attributes.put(SystemConfig.TYPE, "Memory");
        attributes.put(SystemConfig.INITIAL_CONFIGURATION_LOCATION, Objects.requireNonNull(initialConfigUrl).toExternalForm());
        attributes.put(SystemConfig.STARTUP_LOGGED_TO_SYSTEM_OUT, true);
        return attributes;
    }
}
