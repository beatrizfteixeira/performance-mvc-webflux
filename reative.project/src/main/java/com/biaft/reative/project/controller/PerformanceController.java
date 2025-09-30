package com.biaft.reative.project.controller;

import com.biaft.reative.project.dto.DataDto;
import com.biaft.reative.project.metrics.MemoryMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/webflux")
public class PerformanceController {

    private static final int LARGE_PAYLOAD_SIZE = 5000;
    private static final Duration SLOW_IO_DELAY = Duration.ofMillis(200);

    private final MemoryMetricsService memoryMetricsService;

    public PerformanceController(final MemoryMetricsService memoryMetricsService) {
        this.memoryMetricsService = memoryMetricsService;
    }

    @GetMapping("/small-payload/fast-io")
    public Mono<DataDto> getSmallPayloadFastIo() {
        return Mono.defer(() -> {
            this.memoryMetricsService.recordMemoryUsage("webflux-small-payload-fast-io");
            return Mono.just(new DataDto(1, "Small Payload Fast IO Result"));
        });
    }

    @GetMapping("/small-payload/slow-io")
    public Mono<DataDto> getSmallPayloadSlowIo() {
        return Mono.delay(SLOW_IO_DELAY)
                .map(delay -> {
                    this.memoryMetricsService.recordMemoryUsage("webflux-small-payload-slow-io");
                    return new DataDto(1, "Small Payload Slow IO Result");
                });
    }

    @GetMapping("/large-payload/fast-io")
    public Flux<DataDto> getLargePayloadFastIo() {
        return Flux.defer(() -> {
            this.memoryMetricsService.recordMemoryUsage("webflux-large-payload-fast-io");
            return buildLargePayload();
        });
    }

    @GetMapping("/large-payload/slow-io")
    public Flux<DataDto> getLargePayloadSlowIo() {
        return Mono.delay(SLOW_IO_DELAY)
                .flatMapMany(delay -> Flux.defer(() -> {
                    this.memoryMetricsService.recordMemoryUsage("webflux-large-payload-slow-io");
                    return buildLargePayload();
                }));
    }

    private Flux<DataDto> buildLargePayload() {
        return Flux.range(0, LARGE_PAYLOAD_SIZE)
                .map(index -> new DataDto(index, "Item " + index));
    }
}