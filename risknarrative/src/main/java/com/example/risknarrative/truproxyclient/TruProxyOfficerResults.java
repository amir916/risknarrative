package com.example.risknarrative.truproxyclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruProxyOfficerResults (
    @JsonProperty("active_count")
    int activeCount,
    @JsonProperty("total_results")
    int totalResult,
    @JsonProperty("resigned_count")
    int resignedCount,
    List<TruProxyOfficerItem> items
) { }
