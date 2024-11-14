package com.example.risknarrative.repository;

import com.example.risknarrative.dto.Company;
import org.junit.jupiter.api.Test;
import wiremock.org.checkerframework.checker.units.qual.C;

import static com.example.risknarrative.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CompanyRepositoryTest {

    private CompanyRepository companyRepository = new CompanyRepository();

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
        Company companyDTO = getCompanyDTOFromTruProxyDTOs(
                getOneCompanySearchResults().items().get(0), getOfficerSearchResults());

        companyRepository.save(companyDTO);

        // when
        Company company = companyRepository.findCompany(companyDTO.companyNumber());

        assertNotNull(company);
    }

    @Test
    public void shouldNotFindACompanyWhichIsNotSavedInDB(){
        // given
        Company companyDTO = getCompanyDTOFromTruProxyDTOs(
                getOneCompanySearchResults().items().get(0), getOfficerSearchResults());

        companyRepository.save(companyDTO);

        // when
        Company company = companyRepository.findCompany("12345678");

        assertNull(company);
    }
}
