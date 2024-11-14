package com.lexisnexis.risknarrative.api;

import com.lexisnexis.risknarrative.dto.CompanySearchRequest;
import com.lexisnexis.risknarrative.dto.CompanySearchResult;
import com.lexisnexis.risknarrative.service.CompanySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanySearchController {

    @Autowired
    private CompanySearchService companySearchService;

    public CompanySearchController(CompanySearchService companySearchService) {
        this.companySearchService = companySearchService;
    }

    @PostMapping("/searchcompany")
    public ResponseEntity<CompanySearchResult> searchCompany(@RequestBody CompanySearchRequest requestPayload) {

        if (isInValidPayload(requestPayload)) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        return ResponseEntity.ok(companySearchService.searchCompanyDetails(requestPayload));
    }

    private static boolean isInValidPayload(CompanySearchRequest requestPayload) {
        return !StringUtils.hasText(requestPayload.companyName())
                && !StringUtils.hasText(requestPayload.companyNumber());
    }
}
