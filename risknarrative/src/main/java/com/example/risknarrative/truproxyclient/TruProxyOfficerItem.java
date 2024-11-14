package com.example.risknarrative.truproxyclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruProxyOfficerItem (
    String name,
    @JsonProperty("officer_role")
    String officerRole,
    @JsonProperty("appointed_on")
    String appointedOn,
    TruProxyAddress address,
    @JsonProperty("resigned_on")
    String resignedOn
) { }
