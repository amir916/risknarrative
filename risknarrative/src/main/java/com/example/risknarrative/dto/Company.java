package com.example.risknarrative.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "company_number",
        "company_type",
        "title",
        "company_status",
        "date_of_creation"
})
public record Company(
    @JsonProperty("company_number")
    String companyNumber,
    @JsonProperty("company_type")
    String companyType,
    String title,
    @JsonProperty("company_status")
    String companyStatus,
    @JsonProperty("date_of_creation")
    String dateOfCreation,
    Address address,
    List<Officer> officers
){ }


/*    @JsonProperty("company_number")
    private String companyNumber;
    @JsonProperty("company_type")
    public String companyType;
    @JsonProperty("title")
    public String title;
    @JsonProperty("company_status")
    public String companyStatus;
    @JsonProperty("date_of_creation")
    public String dateOfCreation;
    @JsonProperty("address")
    public Address address;
    @JsonProperty("officers")
    public List<Officer> officers;*/
