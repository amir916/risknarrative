package com.example.risknarrative.utils;

import com.example.risknarrative.dto.Address;
import com.example.risknarrative.dto.Company;
import com.example.risknarrative.dto.Officer;
import com.example.risknarrative.truproxyclient.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class TestUtils {
    public static Address createTestAddress(
        String locality,
        String postalCode,
        String premises,
        String addressLine1,
        String country
    ){
        return new Address(
                locality,
                postalCode,
                premises,
                addressLine1,
                country
        );
    }

    public static Officer createTestOfficer(
        String name,
        String officerRole,
        String appointedOn,
        Address address
    ) {
        return new Officer(
                name,
                officerRole,
                appointedOn,
                address
        );
    }

    public static Company createTestItem(
        String companyNumber,
        String companyType,
        String title,
        String companyStatus,
        String dateOfCreation,
        Address address,
        List<Officer> officers
    ){
        return new Company(
                companyNumber,
                companyType,
                title,
                companyStatus,
                dateOfCreation,
                address,
                officers
        );
    }

    public static TruProxyAddress createTruProxyAddress (
        String locality,
        String postalCode,
        String addressLine1,
        String addressLine2,
        String country,
        String premisis
    ) {
        return new TruProxyAddress(locality, postalCode, addressLine1, addressLine2, country, premisis);
    }

    public static TruProxyCompanyItem createTruProxyItem(
            String companyStatus,
            String dateOfCreation,
            String companyNumber,
            String title,
            String companyType,
            TruProxyAddress address
    ) {
        return new TruProxyCompanyItem(
            companyStatus,
            dateOfCreation,
            companyNumber,
            title,
            companyType,
            address
        );
    }

    public static TruProxyCompanyResults createTruProxyCompanySearchResult(
            int totalResults,
            List<TruProxyCompanyItem> items
    ) {
        return new TruProxyCompanyResults(totalResults, items);
    }

    public static TruProxyOfficerItem createTruProxyOfficerItem(
        String name,
        String officerRole,
        String appointedOn,
        TruProxyAddress address,
        String reignedOn
    ){
        return new TruProxyOfficerItem(
                name,
                officerRole,
                appointedOn,
                address,
                reignedOn
        );
    }

    public static TruProxyOfficerResults createTruProxyOfficerResults(
            int activeCount,
            int totalCount,
            int resignedCount,
            List<TruProxyOfficerItem> items
    ) {
        return new TruProxyOfficerResults(
                activeCount,
                totalCount,
                resignedCount,
                items
        );
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static TruProxyCompanyResults getOneCompanySearchResults(){
        TruProxyAddress address = TestUtils.createTruProxyAddress(
                "Retford",
                "DN22 0AD",
                "Boswell Cottage Main Street",
                "North Leverton",
                "England",
                null
        );

        TruProxyCompanyItem item = TestUtils.createTruProxyItem(
                "active",
                "2008-02-11",
                "06500244",
                "BBC LIMITED",
                "ltd",
                address
        );

        return TestUtils.createTruProxyCompanySearchResult(1, List.of(item));
    }

    public static TruProxyOfficerResults getOfficerSearchResults(){

        TruProxyOfficerItem sarah = TestUtils.createTruProxyOfficerItem(
                "BOXALL, Sarah Victoria",
                "secretary",
                "2008-02-11",
                new TruProxyAddress(
                        "London",
                        "SW20 0DP",
                        "Cranford Close",
                        null,
                        "England",
                        "5"
                ),
                null);

        TruProxyOfficerItem simon = TestUtils.createTruProxyOfficerItem(
                "BRAY, Simon Anton",
                "director",
                "2008-02-11",
                new TruProxyAddress(
                        "London",
                        "SW20 0DP",
                        "Cranford Close",
                        null,
                        "England",
                        "5"
                ),
                null);

        TruProxyOfficerItem secretary10 = TestUtils.createTruProxyOfficerItem(
                "FORM 10 SECRETARIES FD LTD",
                "corporate-nominee-secretary",
                "2008-02-11",
                new TruProxyAddress(
                        "Manchester",
                        "M7 4AS",
                        "39a Leicester Road",
                        "Salford",
                        null,
                        null

                ),
                "2008-02-12");

        TruProxyOfficerItem directors10 = TestUtils.createTruProxyOfficerItem(
                "FORM 10 DIRECTORS FD LTD",
                "corporate-nominee-secretary",
                "2008-02-11",
                new TruProxyAddress(
                        "Manchester",
                        "M7 4AS",
                        "39a Leicester Road",
                        "Salford",
                        null,
                        null

                ),
                "2008-02-12");

        return TestUtils.createTruProxyOfficerResults(
                2,
                4,
                2,
                List.of(sarah, simon, secretary10, directors10));
    }

    public static Company getCompanyDTOFromTruProxyDTOs(
            TruProxyCompanyItem truProxyCompanyItem,
            TruProxyOfficerResults truProxyOfficerResults
    ) {
        Address companyAddress = convertToAddressDTO(truProxyCompanyItem.address());
        List<Officer> activeOfficers = toActiveOfficersDTO(truProxyOfficerResults);

        return new Company(
                truProxyCompanyItem.companyNumber(),
                truProxyCompanyItem.companyType(),
                truProxyCompanyItem.title(),
                truProxyCompanyItem.companyStatus(),
                truProxyCompanyItem.dateOfCreation(),
                companyAddress,
                activeOfficers);
    }

    private static Address convertToAddressDTO(TruProxyAddress truCompanyAddress) {
        return new Address(
                truCompanyAddress.locality(),
                truCompanyAddress.postalCode(),
                truCompanyAddress.addressLine1(),
                truCompanyAddress.addressLine2(),
                truCompanyAddress.country()
        );
    }

    private static List<Officer> toActiveOfficersDTO(TruProxyOfficerResults truProxyOfficerResults) {
        List<TruProxyOfficerItem> activeOfficers = truProxyOfficerResults.items().stream()
                .filter(officer -> !StringUtils.hasText(officer.resignedOn()))
                .toList();

        return activeOfficers.stream()
                .map(TestUtils::toOfficerDTO)
                .collect(toList());
    }

    private static Officer toOfficerDTO(TruProxyOfficerItem truProxyOfficerItem){
        return new Officer(
                truProxyOfficerItem.name(),
                truProxyOfficerItem.officerRole(),
                truProxyOfficerItem.appointedOn(),
                toOfficerAddressDTO(truProxyOfficerItem.address())
        );
    }

    private static Address toOfficerAddressDTO(TruProxyAddress truOfficerAddress) {
        return new Address(
                truOfficerAddress.locality(),
                truOfficerAddress.postalCode(),
                truOfficerAddress.premises(),
                truOfficerAddress.addressLine1(),
                truOfficerAddress.country()
        );
    }
}
