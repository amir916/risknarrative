package com.lexisnexis.risknarrative.truproxyclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruProxyAddress (
        String locality,

        @JsonProperty("postal_code")
        String postalCode,

        @JsonProperty("address_line_1")
        String addressLine1,

        @JsonProperty("address_line_2")
        String addressLine2,

        String country,

        String premises
    )
{ }
