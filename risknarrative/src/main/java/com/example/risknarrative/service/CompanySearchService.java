package com.example.risknarrative.service;

import com.example.risknarrative.dto.*;
import com.example.risknarrative.repository.CompanyRepository;
import com.example.risknarrative.truproxyclient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.risknarrative.truproxyclient.TruProxyCompanyStatus.active;
import static java.util.stream.Collectors.toList;

@Service
public class CompanySearchService {

    private TruProxyClient truProxyClient;
    private CompanyRepository companyRepository;

    @Autowired
    public CompanySearchService(
            TruProxyClient truProxyClient,
            CompanyRepository companyRepository
    ) {
        this.truProxyClient = truProxyClient;
        this.companyRepository = companyRepository;
    }

    public CompanySearchResult searchCompanyDetails(CompanySearchRequest request) {
        boolean isCompanyNumberSupplied = StringUtils.hasText(request.companyNumber());
        String searchParam = isCompanyNumberSupplied ? request.companyNumber() : request.companyName();

        if (isCompanyNumberSupplied) {
            Company company = companyRepository.findCompany(searchParam);

            if (company != null) {
                return new CompanySearchResult(1, List.of(company));
            }
        }

        TruProxyCompanyResults truProxyCompanyResults = truProxyClient.searchCompany(searchParam);

        List<TruProxyCompanyItem> truProxyCompanies;
        if (request.activeOnly()) {
            truProxyCompanies = truProxyCompanyResults.items()
                    .stream()
                    .filter(company -> company.companyStatus().equals(active.name()))
                    .collect(toList());
        } else {
            truProxyCompanies = new ArrayList<>(truProxyCompanyResults.items());
        }

        List<Company> companies = new ArrayList<>();
        for (TruProxyCompanyItem truProxyCompanyItem: truProxyCompanies) {
            TruProxyOfficerResults truProxyOfficerResults =
                    truProxyClient.searchOfficers(truProxyCompanyItem.companyNumber());

            Company company = toCompanyDTO(truProxyCompanyItem, truProxyOfficerResults);
            companies.add(company);
            if (isCompanyNumberSupplied) {
                companyRepository.save(company);
            }
        }

        return new CompanySearchResult(truProxyCompanies.size(), companies);
    }

    private Company toCompanyDTO(TruProxyCompanyItem truProxyCompanyItem, TruProxyOfficerResults truProxyOfficerResults) {
        Address companyAddress = toCompanyAddressDTO(truProxyCompanyItem.address());
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

    private List<Officer> toActiveOfficersDTO(TruProxyOfficerResults truProxyOfficerResults) {
        List<TruProxyOfficerItem> activeOfficers = truProxyOfficerResults.items().stream()
                .filter(officer -> !StringUtils.hasText(officer.resignedOn()))
                .toList();

        return activeOfficers.stream()
                .map(this::toOfficerDTO)
                .collect(toList());
    }

    private Officer toOfficerDTO(TruProxyOfficerItem truProxyOfficerItem){
        return new Officer(
            truProxyOfficerItem.name(),
            truProxyOfficerItem.officerRole(),
            truProxyOfficerItem.appointedOn(),
            toOfficerAddressDTO(truProxyOfficerItem.address())
        );
    }

    private Address toOfficerAddressDTO(TruProxyAddress truOfficerAddress) {
        return new Address(
                truOfficerAddress.locality(),
                truOfficerAddress.postalCode(),
                truOfficerAddress.premises(),
                truOfficerAddress.addressLine1(),
                truOfficerAddress.country()
        );
    }

    private Address toCompanyAddressDTO(TruProxyAddress truCompanyAddress) {
        return new Address(
            truCompanyAddress.locality(),
            truCompanyAddress.postalCode(),
            truCompanyAddress.addressLine1(),
            truCompanyAddress.addressLine2(),
            truCompanyAddress.country()
        );
    }
}
