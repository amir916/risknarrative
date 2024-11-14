package com.lexisnexis.risknarrative.repository;

import com.lexisnexis.risknarrative.dto.Company;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CompanyRepository {

    // Using an in-memory Map
    // to store the Company data
    final private Map<String, Company> repository;

    public CompanyRepository(){
        repository = new HashMap<>();
    }

    public void save(Company company) {
        repository.put(company.companyNumber(), company);
    }

    public Company findCompany(String companyNumber) {
        return repository.get(companyNumber);
    }
}
