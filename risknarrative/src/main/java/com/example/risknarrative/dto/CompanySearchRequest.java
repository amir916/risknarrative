package com.example.risknarrative.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompanySearchRequest (
    String companyName,
    String companyNumber,
    boolean activeOnly
) { }
