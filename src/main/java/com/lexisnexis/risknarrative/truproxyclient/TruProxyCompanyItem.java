package com.lexisnexis.risknarrative.truproxyclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TruProxyCompanyItem (
        @JsonProperty("company_status")
        String companyStatus,

        @JsonProperty("date_of_creation")
        String dateOfCreation,

        @JsonProperty("company_number")
        String companyNumber,

        @JsonProperty("title")
        String title,

        @JsonProperty("company_type")
        String companyType,

        @JsonProperty("address")
        TruProxyAddress address
    )
{ }
