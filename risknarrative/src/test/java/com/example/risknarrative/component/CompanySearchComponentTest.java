package com.example.risknarrative.component;

import com.example.risknarrative.dto.CompanySearchRequest;
import com.example.risknarrative.dto.CompanySearchResult;
import com.example.risknarrative.dto.Company;
import com.example.risknarrative.dto.Officer;
import com.example.risknarrative.truproxyclient.TruProxyAddress;
import com.example.risknarrative.truproxyclient.TruProxyCompanyItem;
import com.example.risknarrative.truproxyclient.TruProxyOfficerItem;
import com.example.risknarrative.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wiremock.spring.EnableWireMock;

import java.util.List;

import static com.example.risknarrative.truproxyclient.TruProxyCompanyStatus.active;
import static com.example.risknarrative.utils.TestUtils.asJsonString;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock
public class CompanySearchComponentTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void testCompanySearchHappyPath(){
        // given
        stubFor(get("/Search?Query=06500244")
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(companySearchStub())));

        stubFor(get("/Officers?CompanyNumber=06500244")
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(officerSearchStub())));

        // when
        final HttpEntity<CompanySearchRequest> entity =
                new HttpEntity<>(
                        new CompanySearchRequest("BBC LIMITED","06500244", true)
                );
        final ResponseEntity<CompanySearchResult> responseEntity =
                restTemplate.exchange("/searchcompany", HttpMethod.POST, entity, CompanySearchResult.class);

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        CompanySearchResult result = responseEntity.getBody();
        assertEquals(1, result.totalResults());
        assertEquals(1, result.companies().size());

        Company company = result.companies().get(0);
        assertEquals(active.name(), company.companyStatus());
        assertEquals("ltd", company.companyType());
        assertEquals("BBC LIMITED", company.title());

        assertEquals(2, company.officers().size());
        Officer sarah = company.officers().get(0);
        assertEquals("BOXALL, Sarah Victoria", sarah.name());

        Officer simon = company.officers().get(1);
        assertEquals("BRAY, Simon Anton", simon.name());
    }

    private String companySearchStub(){
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

        return asJsonString(TestUtils.createTruProxyCompanySearchResult(1, List.of(item)));
    }

    private String officerSearchStub(){

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

        return asJsonString(TestUtils.createTruProxyOfficerResults(
                2,
                4,
                2,
                List.of(sarah, simon, secretary10, directors10)));
    }
}
