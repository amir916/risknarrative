package com.example.risknarrative.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Address (
        String locality,
        @JsonProperty("postal_code")
        String postalCode,
        String premises,
        @JsonProperty("address_line_1")
        String addressLine1,
        String country
) { }
