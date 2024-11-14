package com.lexisnexis.risknarrative.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "total_results",
        "items"
})
public record CompanySearchResult (
    @JsonProperty("total_results")
    Integer totalResults,
    @JsonProperty("items")
    List<Company> companies
) {

}


