package com.biaft.mvc.roject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class DataDto {
    private int id;
    private String name;
    private long timestamp;

    public DataDto(int id, String name) {
        this.id = id;
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
    }
}