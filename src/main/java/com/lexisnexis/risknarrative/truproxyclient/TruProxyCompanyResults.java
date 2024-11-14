package com.lexisnexis.risknarrative.truproxyclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruProxyCompanyResults (
    @JsonProperty("total_results")
    int totalResults,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<TruProxyCompanyItem> items
)
{ }
