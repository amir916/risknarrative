package com.example.risknarrative.truproxyclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class TruProxyClient {

    @Value("${trunarrative.api.url}")
    String truNarrativUrl;

    @Value("${trunarrative.api.key}")
    String apiKey;

    private RestTemplate restTemplate;

    @Autowired
    public TruProxyClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public TruProxyCompanyResults searchCompany(String companyNameOrNumber) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            final ResponseEntity<TruProxyCompanyResults> result =
                    restTemplate.exchange(truNarrativUrl + "/Search?Query=" + companyNameOrNumber, HttpMethod.GET, entity, TruProxyCompanyResults.class);

            if (result.getBody().totalResults() == 0)
                return new TruProxyCompanyResults(0, List.of());
            else
                return result.getBody();
        } catch (RestClientException exception) {
            log.info("Service Call {} Failed. Error: {}", truNarrativUrl + "/Search", exception.getLocalizedMessage());
            return new TruProxyCompanyResults(0, List.of());
        } catch (Exception exception) {
            //TODO NOTE: It depends usually on the business scenario when to recover and when to fail.
            // However, for the sake of this test, I have assuemd that for network error (i.e., RestClientException),
            // we decided to recover and for anything else (i.e., Exception) our decision was to fail.

            log.info("Service Call {} Failed. Unknown error: {}", truNarrativUrl + "/Search", exception.getLocalizedMessage());
            throw new TruProxyClientException(exception.getMessage(), exception.getCause());
        }
    }

    public TruProxyOfficerResults searchOfficers(String companyNumber) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        final HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            final ResponseEntity<TruProxyOfficerResults> result =
                    restTemplate.exchange(truNarrativUrl + "/Officers?CompanyNumber=" + companyNumber, HttpMethod.GET, entity, TruProxyOfficerResults.class);

            if (result.getBody().totalResult() == 0)
                return new TruProxyOfficerResults(0, 0, 0, List.of());
            else
                return result.getBody();
        } catch (RestClientException exception) {
            log.info("Service Call {} Failed. Error: {}", truNarrativUrl + "/Officers", exception.getLocalizedMessage());
            return new TruProxyOfficerResults(0, 0, 0, List.of());
        } catch (Exception exception) {
            //TODO NOTE: It depends usually on the business scenario when to recover and when to fail.
            // However, for the sake of this test, I have assuemd that for network error (i.e., RestClientException),
            // we decided to recover and for anything else (i.e., Exception) our decision was to fail.

            log.info("Service Call {} Failed. Unknown error: {}", truNarrativUrl + "/Officers", exception.getLocalizedMessage());
            throw new TruProxyClientException(exception.getMessage(), exception.getCause());
        }
    }
}
