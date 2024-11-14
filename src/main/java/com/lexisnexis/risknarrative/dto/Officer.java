package com.lexisnexis.risknarrative.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Officer (
        String name,
        @JsonProperty("officer_role")
        String officerRole,
        @JsonProperty("appointed_on")
        String appointedOn,
        Address address
) {

}

/*    @JsonProperty("name")
    private String name;
    @JsonProperty("officer_role")
    private String officerRole;
    @JsonProperty("appointed_on")
    private String appointedOn;
    @JsonProperty("address")
    private Address address;*/