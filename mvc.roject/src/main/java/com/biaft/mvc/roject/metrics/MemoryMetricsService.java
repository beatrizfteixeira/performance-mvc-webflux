package com.biaft.mvc.roject.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
public class MemoryMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryMetricsService.class);
    private final Path metricsFile;

    public MemoryMetricsService() {
        final Path metricsDirectory = Paths.get("metrics");
        try {
            Files.createDirectories(metricsDirectory);
        } catch (final IOException exception) {
            LOGGER.warn("Unable to create metrics directory", exception);
        }
        this.metricsFile = metricsDirectory.resolve("mvc-memory-metrics.csv");
        if (!Files.exists(this.metricsFile)) {
            try {
                Files.write(this.metricsFile,
                        "timestamp,endpoint,heapUsedBytes,heapCommittedBytes\n".getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE);
            } catch (final IOException exception) {
                LOGGER.warn("Unable to initialize metrics file", exception);
            }
        }
    }

    public void recordMemoryUsage(final String endpoint) {
        CompletableFuture.runAsync(() -> writeUsage(endpoint));
    }

    private void writeUsage(final String endpoint) {
        final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        final MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        final String line = String.format("%s,%s,%d,%d%n",
                LocalDateTime.now(),
                endpoint,
                heapUsage.getUsed(),
                heapUsage.getCommitted());
        try {
            Files.write(this.metricsFile,
                    line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (final IOException exception) {
            LOGGER.warn("Unable to record memory usage", exception);
        }
    }
}

