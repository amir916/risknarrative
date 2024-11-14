package com.example.risknarrative.api;

import com.example.risknarrative.dto.*;
import com.example.risknarrative.service.CompanySearchService;
import com.example.risknarrative.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.risknarrative.utils.TestUtils.asJsonString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class CompanySearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanySearchService companySearchService;

    @Test
    public void shouldReturnBadRequestWhenCompanyNameAndNumberAreNotSupplied() throws Exception {
        mockMvc.perform(
                    post("/searchcompany")
                    .content(asJsonString(new CompanySearchRequest("","", false)))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnSuccessResultsWhenCompanyNameIsSupplied() throws Exception {
        // given
        CompanySearchRequest request = new CompanySearchRequest("A Company", "", false);
        when(companySearchService.searchCompanyDetails(request)).thenReturn(mockSearchResults());

        // when
        mockMvc.perform(
                    post("/searchcompany")
                    .content(asJsonString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_results").value(1))
                .andExpect(jsonPath("$.items").isArray());

        // then
        verify(companySearchService).searchCompanyDetails(request);
    }

    @Test
    public void shouldReturnSuccessResultsWhenCompanyNumberIsSupplied() throws Exception {
        // given
        CompanySearchRequest request = new CompanySearchRequest("", "2424242", false);
        when(companySearchService.searchCompanyDetails(request)).thenReturn(mockSearchResults());

        // when
        mockMvc.perform(
                        post("/searchcompany")
                                .content(asJsonString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_results").value(1))
                .andExpect(jsonPath("$.items").isArray());

        // then
        verify(companySearchService).searchCompanyDetails(request);
    }

    private CompanySearchResult mockSearchResults(){
        Address companyAddress = TestUtils.createTestAddress(
                "Retford",
                "DN22 0AD",
                "Boswell Cottage Main Street",
                "North Leverton", "England"
        );

        Address officerAddress = TestUtils.createTestAddress(
                "London",
                "SW20 0DP",
                "5",
                "Cranford Close", "England"
        );

        Officer officer = TestUtils.createTestOfficer(
                "BOXALL, Sarah Victoria",
                "secretary",
                "2008-02-11",
                officerAddress
        );

        Company company = TestUtils.createTestItem(
                "06500244",
                "ltd",
                "BBC Limited",
                "active",
                "2008-02-11",
                companyAddress,
                List.of(officer)
        );

        return new CompanySearchResult(1, List.of(company));
    }
}
