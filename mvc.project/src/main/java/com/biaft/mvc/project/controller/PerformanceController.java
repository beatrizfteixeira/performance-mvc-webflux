package com.biaft.mvc.project.controller;

import com.biaft.mvc.project.dto.DataDto;
import com.biaft.mvc.project.metrics.MemoryMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mvc")
public class PerformanceController {

    private static final int LARGE_PAYLOAD_SIZE = 5000;
    private static final int SLOW_IO_DELAY_MILLIS = 200;

    private final MemoryMetricsService memoryMetricsService;

    public PerformanceController(final MemoryMetricsService memoryMetricsService) {
        this.memoryMetricsService = memoryMetricsService;
    }

    @GetMapping("/small-payload/fast-io")
    public DataDto getSmallPayloadFastIo() {
        return new DataDto(1, "Small Payload Fast IO Result");
    }

    @GetMapping("/small-payload/slow-io")
    public DataDto getSmallPayloadSlowIo() throws InterruptedException {
        Thread.sleep(SLOW_IO_DELAY_MILLIS);
        return new DataDto(1, "Small Payload Slow IO Result");
    }

    @GetMapping("/large-payload/fast-io")
    public List<DataDto> getLargePayloadFastIo() {
        final List<DataDto> dataList = buildLargePayload();
        this.memoryMetricsService.recordMemoryUsage("mvc-large-payload-fast-io");
        return dataList;
    }

    @GetMapping("/large-payload/slow-io")
    public List<DataDto> getLargePayloadSlowIo() throws InterruptedException {
        Thread.sleep(SLOW_IO_DELAY_MILLIS);
        final List<DataDto> dataList = buildLargePayload();
        this.memoryMetricsService.recordMemoryUsage("mvc-large-payload-slow-io");
        return dataList;
    }

    private List<DataDto> buildLargePayload() {
        final List<DataDto> dataList = new ArrayList<>(LARGE_PAYLOAD_SIZE);
        for (int i = 0; i < LARGE_PAYLOAD_SIZE; i++) {
            dataList.add(new DataDto(i, "Item " + i));
        }
        return dataList;
    }
}