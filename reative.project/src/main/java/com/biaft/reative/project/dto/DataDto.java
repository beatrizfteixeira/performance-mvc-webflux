package com.biaft.reative.project.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataDto {
    private int id;
    private String name;
    private long timestamp;

    public DataDto() {
    }

    public DataDto(int id, String name) {
        this.id = id;
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
    }
}