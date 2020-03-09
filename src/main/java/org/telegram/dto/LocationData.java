package org.telegram.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class LocationData {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> states;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String country;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long totalCases;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long differenceWithYesterday;
}
