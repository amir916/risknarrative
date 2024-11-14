package com.lexisnexis.risknarrative.truproxyclient;

import com.lexisnexis.risknarrative.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TruProxyClientTest {

    //TODO inject properties via test annotaions
    private final String mockedTrunarrativeApiUrl = "http://trunarritive.cloud";
    private final String mockedApiKey = "mockedKey";

    @Mock
    private RestTemplate restTemplate;

    private TruProxyClient truProxyClient;

    @BeforeEach
    public void Setup(){
        truProxyClient = new TruProxyClient(restTemplate);
        truProxyClient.truNarrativUrl = mockedTrunarrativeApiUrl;
        truProxyClient.apiKey = mockedApiKey;
    }

    @Test
    public void shouldReturnCompanyResultsSuccessfully() {
        // given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", mockedApiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        String companyNumber = "123456";
        when(
            restTemplate.exchange(
                eq(mockedTrunarrativeApiUrl + "/Search?Query="+companyNumber),
                eq(HttpMethod.GET),
                eq(entity),
                eq(TruProxyCompanyResults.class))).
        thenReturn(ResponseEntity.ok(TestUtils.getOneCompanySearchResults()));

        // when
        TruProxyCompanyResults result = truProxyClient.searchCompany(companyNumber);

        // then
        assertNotNull(result);
        assertEquals(1, result.totalResults());
        assertEquals(1, result.items().size());

        verify(restTemplate)
            .exchange(
                eq(mockedTrunarrativeApiUrl + "/Search?Query="+companyNumber),
                eq(HttpMethod.GET),
                eq(entity),
                eq(TruProxyCompanyResults.class));
    }

    @Test
    public void shouldReturnNoCompanyResultsWhenNoResultsFound() {
        // given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", mockedApiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        String companyNumber = "123456";
        when(
                restTemplate.exchange(
                        eq(mockedTrunarrativeApiUrl + "/Search?Query="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyCompanyResults.class))).
        thenReturn(ResponseEntity.ok(new TruProxyCompanyResults(0, null)));

        // when
        TruProxyCompanyResults result = truProxyClient.searchCompany(companyNumber);

        // then
        assertNotNull(result);
        assertEquals(0, result.totalResults());
        assertEquals(0, result.items().size());

        verify(restTemplate)
                .exchange(
                        eq(mockedTrunarrativeApiUrl + "/Search?Query="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyCompanyResults.class));
    }

    @Test
    public void shouldReturnNoCompanyResultsWhenThirdPartyCallFails() {
        // given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", mockedApiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        String companyNumber = "123456";
        when(
                restTemplate.exchange(
                        eq(mockedTrunarrativeApiUrl + "/Search?Query="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyCompanyResults.class))).
        thenThrow(RestClientException.class);

        // when
        TruProxyCompanyResults result = truProxyClient.searchCompany(companyNumber);

        // then
        assertNotNull(result);
        assertEquals(0, result.totalResults());
        assertEquals(0, result.items().size());

        verify(restTemplate)
                .exchange(
                        eq(mockedTrunarrativeApiUrl + "/Search?Query="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyCompanyResults.class));
    }

    @Test
    public void shouldFailWhenUknownExceptionOccurs() {
        // given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", mockedApiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        String companyNumber = "123456";
        when(
                restTemplate.exchange(
                        eq(mockedTrunarrativeApiUrl + "/Search?Query="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyCompanyResults.class))).
                thenThrow(RuntimeException.class);

        // when
        assertThrows(TruProxyClientException.class, () -> truProxyClient.searchCompany(companyNumber));

        // then
        verify(restTemplate)
                .exchange(
                        eq(mockedTrunarrativeApiUrl + "/Search?Query="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyCompanyResults.class)
                );
    }

    @Test
    public void shouldReturnOfficersResultsSuccessfully() {
        // given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", mockedApiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        String companyNumber = "123456";
        when(
                restTemplate.exchange(
                        eq(mockedTrunarrativeApiUrl + "/Officers?CompanyNumber="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyOfficerResults.class))).
                thenReturn(ResponseEntity.ok(TestUtils.getOfficerSearchResults()));

        // when
        TruProxyOfficerResults result = truProxyClient.searchOfficers(companyNumber);

        // then
        assertNotNull(result);
        assertEquals(4, result.totalResult());
        assertEquals(4, result.items().size());

        verify(restTemplate)
                .exchange(
                        eq(mockedTrunarrativeApiUrl + "/Officers?CompanyNumber="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyOfficerResults.class)
                );
    }

    @Test
    public void shouldReturnNoOfficersResultsWhenNoOfficersFound() {
        // given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", mockedApiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        String companyNumber = "123456";
        when(
                restTemplate.exchange(
                        eq(mockedTrunarrativeApiUrl + "/Officers?CompanyNumber="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyOfficerResults.class))).
                thenReturn(ResponseEntity.ok(new TruProxyOfficerResults(0, 0, 0, null)));

        // when
        TruProxyOfficerResults result = truProxyClient.searchOfficers(companyNumber);

        // then
        assertNotNull(result);
        assertEquals(0, result.totalResult());
        assertEquals(0, result.items().size());

        verify(restTemplate)
                .exchange(
                        eq(mockedTrunarrativeApiUrl + "/Officers?CompanyNumber="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyOfficerResults.class)
                );
    }

    @Test
    public void shouldReturnNoOfficersResultsWhenThirdPartyCallFails() {
        // given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", mockedApiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        String companyNumber = "123456";
        when(
                restTemplate.exchange(
                        eq(mockedTrunarrativeApiUrl + "/Officers?CompanyNumber="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyOfficerResults.class))).
                thenThrow(RestClientException.class);

        // when
        TruProxyOfficerResults result = truProxyClient.searchOfficers(companyNumber);

        // then
        assertNotNull(result);
        assertEquals(0, result.totalResult());
        assertEquals(0, result.items().size());

        verify(restTemplate)
            .exchange(
                eq(mockedTrunarrativeApiUrl + "/Officers?CompanyNumber="+companyNumber),
                eq(HttpMethod.GET),
                eq(entity),
                eq(TruProxyOfficerResults.class)
            );
    }

    @Test
    public void shouldFailWhenUknownExceptionOccursForOfficersSearch() {
        // given
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", mockedApiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        String companyNumber = "123456";
        when(
                restTemplate.exchange(
                        eq(mockedTrunarrativeApiUrl + "/Officers?CompanyNumber="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyOfficerResults.class))).
                thenThrow(RuntimeException.class);

        // when
        assertThrows(TruProxyClientException.class, () -> truProxyClient.searchOfficers(companyNumber));

        // then
        verify(restTemplate)
                .exchange(
                        eq(mockedTrunarrativeApiUrl + "/Officers?CompanyNumber="+companyNumber),
                        eq(HttpMethod.GET),
                        eq(entity),
                        eq(TruProxyOfficerResults.class)
                );
    }
}
