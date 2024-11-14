package com.example.risknarrative.service;

import com.example.risknarrative.dto.*;
import com.example.risknarrative.repository.CompanyRepository;
import com.example.risknarrative.truproxyclient.*;
import com.example.risknarrative.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.risknarrative.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanySearchServiceTest {

    @Mock
    private TruProxyClient truProxyClient;

    @Mock
    private CompanyRepository companyRepository;

    private CompanySearchService companySearchService;

    @BeforeEach
    public void setUp(){
        companySearchService = new CompanySearchService(truProxyClient, companyRepository);
    }

    @Test
    public void shouldUseCompanyNameWhenOnlyCompanyNameIsProvided() {
        // given
        String companyName = "BBC Limited";
        CompanySearchRequest request = new CompanySearchRequest(companyName, "", true);

        TruProxyCompanyResults stubbedCompanyResults = getOneCompanySearchResults();
        TruProxyOfficerResults stubbedOfficerResults = getOfficerSearchResults();
        when(truProxyClient.searchCompany(anyString())).thenReturn(stubbedCompanyResults);
        when(truProxyClient.searchOfficers(anyString())).thenReturn(stubbedOfficerResults);

        // when
        companySearchService.searchCompanyDetails(request);

        // then
        verify(truProxyClient).searchCompany(eq(companyName));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    public void shouldUseCompanyNumberWhenBothFieldsAreProvided() {
        // given
        String companyName = "BBC Limited";
        String companyNumber = "06500244";
        CompanySearchRequest request = new CompanySearchRequest(companyName, companyNumber, true);

        TruProxyCompanyResults stubbedCompanyResults = getOneCompanySearchResults();
        TruProxyOfficerResults stubbedOfficerResults = getOfficerSearchResults();
        Company companyDTO = getCompanyDTOFromTruProxyDTOs(stubbedCompanyResults.items().get(0), stubbedOfficerResults);

        when(truProxyClient.searchCompany(eq(companyNumber))).thenReturn(stubbedCompanyResults);
        when(truProxyClient.searchOfficers(eq(companyNumber))).thenReturn(stubbedOfficerResults);

        // when
        companySearchService.searchCompanyDetails(request);

        // then
        verify(truProxyClient).searchCompany(eq(companyNumber));
        verify(companyRepository).save(companyDTO);
    }

    @Test
    public void shouldFindAndReturnCompanyFromDBWhenBothFieldsAreProvided() {
        // given
        String companyName = "BBC Limited";
        String companyNumber = "06500244";
        CompanySearchRequest request = new CompanySearchRequest(companyName, companyNumber, true);

        Company companyDTO = getCompanyDTOFromTruProxyDTOs(
                getOneCompanySearchResults().items().get(0), getOfficerSearchResults());

        when(companyRepository.findCompany(companyNumber)).thenReturn(companyDTO);

        // when
        CompanySearchResult result = companySearchService.searchCompanyDetails(request);
        assertNotNull(result);
        assertEquals(1, result.totalResults());
        assertEquals(1, result.companies().size());
        assertEquals(2, result.companies().get(0).officers().size());

        // then
        verify(truProxyClient, never()).searchCompany(eq(companyNumber));
        verify(truProxyClient, never()).searchOfficers(eq(companyNumber));
        verify(companyRepository, never()).save(companyDTO);
    }

    @Test
    public void shouldReturnActiveCompaniesAndOfficersWhenRequestContainsCompanyNumberAndActiveOnlyIsTrue(){
        // given
        String companyName = "BBC Limited";
        String companyNumber = "06500244";
        TruProxyCompanyResults stubbedCompanyResults = getOneCompanySearchResults();
        TruProxyOfficerResults stubbedOfficerResults = getOfficerSearchResults();

        CompanySearchRequest request = new CompanySearchRequest(companyName, companyNumber, true);
        when(truProxyClient.searchCompany(eq(companyNumber))).thenReturn(stubbedCompanyResults);
        when(truProxyClient.searchOfficers(eq(companyNumber))).thenReturn(stubbedOfficerResults);

        // when
        CompanySearchResult result = companySearchService.searchCompanyDetails(request);

        // then
        assertNotNull(result);
        assertEquals(1, result.totalResults());
        assertEquals(1, result.companies().size());

        TruProxyCompanyItem expectedCompanyResult = stubbedCompanyResults.items().get(0);
        Company actualCompanyResult = result.companies().get(0);
        assertEquals(expectedCompanyResult.companyNumber(), actualCompanyResult.companyNumber());
        assertEquals(expectedCompanyResult.companyType(), actualCompanyResult.companyType());
        assertEquals(expectedCompanyResult.title(), actualCompanyResult.title());
        assertEquals(expectedCompanyResult.companyStatus(), actualCompanyResult.companyStatus());
        assertEquals(expectedCompanyResult.dateOfCreation(), actualCompanyResult.dateOfCreation());

        TruProxyAddress expectedCompanyAddress = expectedCompanyResult.address();
        Address actualCompanyAddress = actualCompanyResult.address();
        assertEquals(expectedCompanyAddress.locality(), actualCompanyAddress.locality());
        assertEquals(expectedCompanyAddress.postalCode(), actualCompanyAddress.postalCode());
        assertEquals(expectedCompanyAddress.addressLine1(), actualCompanyAddress.premises());
        assertEquals(expectedCompanyAddress.addressLine2(), actualCompanyAddress.addressLine1());
        assertEquals(expectedCompanyAddress.country(), actualCompanyAddress.country());

        assertEquals(2, actualCompanyResult.officers().size());
        Officer actualSarah = actualCompanyResult.officers().get(0);
        TruProxyOfficerItem stubbedSarah = stubbedOfficerResults.items().get(0);
        assertEquals(stubbedSarah.name(), actualSarah.name());
        assertEquals(stubbedSarah.officerRole(), actualSarah.officerRole());
        assertEquals(stubbedSarah.appointedOn(), actualSarah.appointedOn());

        TruProxyAddress stubbedSarahAddress = stubbedSarah.address();
        Address actualSarahAddress = actualSarah.address();
        assertEquals(stubbedSarahAddress.premises(), actualSarahAddress.premises());
        assertEquals(stubbedSarahAddress.addressLine1(), actualSarahAddress.addressLine1());
        assertEquals(stubbedSarahAddress.locality(), actualSarahAddress.locality());
        assertEquals(stubbedSarahAddress.postalCode(), actualSarahAddress.postalCode());
        assertEquals(stubbedSarahAddress.country(), actualSarahAddress.country());

        Officer actualSimon = actualCompanyResult.officers().get(1);
        TruProxyOfficerItem stubbedSimon = stubbedOfficerResults.items().get(1);
        assertEquals(stubbedSimon.name(), actualSimon.name());
        assertEquals(stubbedSimon.officerRole(), actualSimon.officerRole());
        assertEquals(stubbedSimon.appointedOn(), actualSimon.appointedOn());

        TruProxyAddress stubbedSimonddress = stubbedSimon.address();
        Address actualSimonAddress = actualSimon.address();
        assertEquals(stubbedSimonddress.premises(), actualSimonAddress.premises());
        assertEquals(stubbedSimonddress.addressLine1(), actualSimonAddress.addressLine1());
        assertEquals(stubbedSimonddress.locality(), actualSimonAddress.locality());
        assertEquals(stubbedSimonddress.postalCode(), actualSimonAddress.postalCode());
        assertEquals(stubbedSimonddress.country(), actualSimonAddress.country());

        verify(truProxyClient).searchCompany(eq(companyNumber));
        verify(truProxyClient).searchOfficers(eq(companyNumber));
    }

    @Test
    public void shouldReturnAllCompaniesWhenRequestContainsCompanyNameAndActiveOnlyIsFalse(){
        // given
        String companyName = "BBC Limited";
        String companyNumber1 = "06500244";
        String companyNumber2 = "12990923";
        String companyNumber3 = "07215276";
        TruProxyCompanyResults stubbedCompanyResults = getMultipleCompanySearchResults(companyNumber1, companyNumber2, companyNumber3);
        TruProxyOfficerResults stubbedOfficerResults = getOfficerSearchResults();
        CompanySearchRequest request = new CompanySearchRequest(companyName, "", false);
        when(truProxyClient.searchCompany(eq(companyName))).thenReturn(stubbedCompanyResults);
        when(truProxyClient.searchOfficers(eq(companyNumber1))).thenReturn(stubbedOfficerResults);
        when(truProxyClient.searchOfficers(eq(companyNumber2))).thenReturn(stubbedOfficerResults);
        when(truProxyClient.searchOfficers(eq(companyNumber3))).thenReturn(stubbedOfficerResults);

        // when
        CompanySearchResult result = companySearchService.searchCompanyDetails(request);

        // then
        assertNotNull(result);
        assertEquals(3, result.totalResults());
        assertEquals(3, result.companies().size());

        TruProxyCompanyItem bbcLtdCompanyResult = stubbedCompanyResults.items().get(0);
        Company actualCompanyResult = result.companies().get(0);
        assertEquals(bbcLtdCompanyResult.companyNumber(), actualCompanyResult.companyNumber());
        assertEquals(bbcLtdCompanyResult.companyType(), actualCompanyResult.companyType());
        assertEquals(bbcLtdCompanyResult.title(), actualCompanyResult.title());
        assertEquals(bbcLtdCompanyResult.companyStatus(), actualCompanyResult.companyStatus());
        assertEquals(bbcLtdCompanyResult.dateOfCreation(), actualCompanyResult.dateOfCreation());

        TruProxyAddress bbcLimitedCompanyAddress = bbcLtdCompanyResult.address();
        Address actualCompanyAddress = actualCompanyResult.address();
        assertEquals(bbcLimitedCompanyAddress.locality(), actualCompanyAddress.locality());
        assertEquals(bbcLimitedCompanyAddress.postalCode(), actualCompanyAddress.postalCode());
        assertEquals(bbcLimitedCompanyAddress.addressLine1(), actualCompanyAddress.premises());
        assertEquals(bbcLimitedCompanyAddress.addressLine2(), actualCompanyAddress.addressLine1());
        assertEquals(bbcLimitedCompanyAddress.country(), actualCompanyAddress.country());

        assertEquals(2, actualCompanyResult.officers().size());
        Officer actualSarah = actualCompanyResult.officers().get(0);
        TruProxyOfficerItem stubbedSarah = stubbedOfficerResults.items().get(0);
        assertEquals(stubbedSarah.name(), actualSarah.name());
        assertEquals(stubbedSarah.officerRole(), actualSarah.officerRole());
        assertEquals(stubbedSarah.appointedOn(), actualSarah.appointedOn());

        TruProxyAddress stubbedSarahAddress = stubbedSarah.address();
        Address actualSarahAddress = actualSarah.address();
        assertEquals(stubbedSarahAddress.premises(), actualSarahAddress.premises());
        assertEquals(stubbedSarahAddress.addressLine1(), actualSarahAddress.addressLine1());
        assertEquals(stubbedSarahAddress.locality(), actualSarahAddress.locality());
        assertEquals(stubbedSarahAddress.postalCode(), actualSarahAddress.postalCode());
        assertEquals(stubbedSarahAddress.country(), actualSarahAddress.country());

        Officer actualSimon = actualCompanyResult.officers().get(1);
        TruProxyOfficerItem stubbedSimon = stubbedOfficerResults.items().get(1);
        assertEquals(stubbedSimon.name(), actualSimon.name());
        assertEquals(stubbedSimon.officerRole(), actualSimon.officerRole());
        assertEquals(stubbedSimon.appointedOn(), actualSimon.appointedOn());

        TruProxyAddress stubbedSimonddress = stubbedSimon.address();
        Address actualSimonAddress = actualSimon.address();
        assertEquals(stubbedSimonddress.premises(), actualSimonAddress.premises());
        assertEquals(stubbedSimonddress.addressLine1(), actualSimonAddress.addressLine1());
        assertEquals(stubbedSimonddress.locality(), actualSimonAddress.locality());
        assertEquals(stubbedSimonddress.postalCode(), actualSimonAddress.postalCode());
        assertEquals(stubbedSimonddress.country(), actualSimonAddress.country());

        TruProxyCompanyItem bbcConstrutionLimitedExpectedResult = stubbedCompanyResults.items().get(1);
        Company bbcConstrutionLimitedActualResult = result.companies().get(1);
        assertEquals(bbcConstrutionLimitedExpectedResult.companyNumber(), bbcConstrutionLimitedActualResult.companyNumber());
        assertEquals(bbcConstrutionLimitedExpectedResult.companyType(), bbcConstrutionLimitedActualResult.companyType());
        assertEquals(bbcConstrutionLimitedExpectedResult.title(), bbcConstrutionLimitedActualResult.title());
        assertEquals(bbcConstrutionLimitedExpectedResult.companyStatus(), bbcConstrutionLimitedActualResult.companyStatus());
        assertEquals(bbcConstrutionLimitedExpectedResult.dateOfCreation(), bbcConstrutionLimitedActualResult.dateOfCreation());

        TruProxyAddress bbcConstructionLimitedExpectedAddress = bbcConstrutionLimitedExpectedResult.address();
        Address bbcConstructionLimitedActualAddress = bbcConstrutionLimitedActualResult.address();
        assertEquals(bbcConstructionLimitedExpectedAddress.locality(), bbcConstructionLimitedActualAddress.locality());
        assertEquals(bbcConstructionLimitedExpectedAddress.postalCode(), bbcConstructionLimitedActualAddress.postalCode());
        assertEquals(bbcConstructionLimitedExpectedAddress.addressLine1(), bbcConstructionLimitedActualAddress.premises());
        assertEquals(bbcConstructionLimitedExpectedAddress.addressLine2(), bbcConstructionLimitedActualAddress.addressLine1());
        assertEquals(bbcConstructionLimitedExpectedAddress.country(), bbcConstructionLimitedActualAddress.country());

        TruProxyCompanyItem bbcApplicationLtdExpectedResult = stubbedCompanyResults.items().get(2);
        Company bbcApplicationLimitedExpectedResult = result.companies().get(2);
        assertEquals(bbcApplicationLtdExpectedResult.companyNumber(), bbcApplicationLimitedExpectedResult.companyNumber());
        assertEquals(bbcApplicationLtdExpectedResult.companyType(), bbcApplicationLimitedExpectedResult.companyType());
        assertEquals(bbcApplicationLtdExpectedResult.title(), bbcApplicationLimitedExpectedResult.title());
        assertEquals(bbcApplicationLtdExpectedResult.companyStatus(), bbcApplicationLimitedExpectedResult.companyStatus());
        assertEquals(bbcApplicationLtdExpectedResult.dateOfCreation(), bbcApplicationLimitedExpectedResult.dateOfCreation());

        TruProxyAddress bbcApplicationLimitedExpectedAddress = bbcApplicationLtdExpectedResult.address();
        Address bbcApplicatoinActualAddress = bbcApplicationLimitedExpectedResult.address();
        assertEquals(bbcApplicationLimitedExpectedAddress.locality(), bbcApplicatoinActualAddress.locality());
        assertEquals(bbcApplicationLimitedExpectedAddress.postalCode(), bbcApplicatoinActualAddress.postalCode());
        assertEquals(bbcApplicationLimitedExpectedAddress.addressLine1(), bbcApplicatoinActualAddress.premises());
        assertEquals(bbcApplicationLimitedExpectedAddress.addressLine2(), bbcApplicatoinActualAddress.addressLine1());
        assertEquals(bbcApplicationLimitedExpectedAddress.country(), bbcApplicatoinActualAddress.country());

        verify(truProxyClient).searchCompany(eq(companyName));
        verify(truProxyClient).searchOfficers(eq(companyNumber1));
        verify(truProxyClient).searchOfficers(eq(companyNumber2));
        verify(truProxyClient).searchOfficers(eq(companyNumber3));
    }

    private TruProxyCompanyResults getMultipleCompanySearchResults(
            String compnayNumber1,
            String compnayNumber2,
            String compnayNumber3
    ){
        TruProxyAddress bbcLimitedAddress = TestUtils.createTruProxyAddress(
                "Retford",
                "DN22 0AD",
                "Boswell Cottage Main Street",
                "North Leverton",
                "England",
                null
        );

        TruProxyCompanyItem bbcLimited = TestUtils.createTruProxyItem(
                "active",
                "2008-02-11",
                compnayNumber1,
                "BBC LIMITED",
                "ltd",
                bbcLimitedAddress
        );

        TruProxyAddress bbcConstructionAddress = TestUtils.createTruProxyAddress(
                "Retford",
                "W5 2AR",
                "Gordon Road",
                "",
                "England",
                "52a"
        );

        TruProxyCompanyItem bbcConstrutionLimited = TestUtils.createTruProxyItem(
                "active",
                "2020-11-02",
                compnayNumber2,
                "BASEMENTS SOLUTIONS LIMITED",
                "ltd",
                bbcConstructionAddress
        );

        TruProxyAddress bbcApplicationAddress = TestUtils.createTruProxyAddress(
                "Bingley",
                "BD16 2RS",
                "Longwood Hall",
                "",
                "England",
                "2"
        );

        TruProxyCompanyItem bbcApplicationLimited = TestUtils.createTruProxyItem(
                "dissolved",
                "2010-14-07",
                compnayNumber3,
                "BASEMENTS APPLICATION LIMITED",
                "ltd",
                bbcApplicationAddress
        );

        return TestUtils.createTruProxyCompanySearchResult(3, List.of(bbcLimited, bbcConstrutionLimited, bbcApplicationLimited));
    }
}
