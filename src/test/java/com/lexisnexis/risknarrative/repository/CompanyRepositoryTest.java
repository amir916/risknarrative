package com.lexisnexis.risknarrative.repository;

import com.lexisnexis.risknarrative.dto.Company;
import com.lexisnexis.risknarrative.utils.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CompanyRepositoryTest {

    private final CompanyRepository companyRepository = new CompanyRepository();

    @Test
    public void shouldNotFindACompanyInEmptyDB(){
        // given
        // when
        Company company = companyRepository.findCompany("123456");

        // then
        assertNull(company);
    }

    @Test
    public void shouldFindACompanyWhenItWasPreviouslySaved(){
        // given
        Company companyDTO = TestUtils.getCompanyDTOFromTruProxyDTOs(
                TestUtils.getOneCompanySearchResults().items().get(0), TestUtils.getOfficerSearchResults());

        companyRepository.save(companyDTO);

        // when
        Company company = companyRepository.findCompany(companyDTO.companyNumber());

        assertNotNull(company);
    }

    @Test
    public void shouldNotFindACompanyWhichIsNotSavedInDB(){
        // given
        Company companyDTO = TestUtils.getCompanyDTOFromTruProxyDTOs(
                TestUtils.getOneCompanySearchResults().items().get(0), TestUtils.getOfficerSearchResults());

        companyRepository.save(companyDTO);

        // when
        Company company = companyRepository.findCompany("12345678");

        assertNull(company);
    }
}
