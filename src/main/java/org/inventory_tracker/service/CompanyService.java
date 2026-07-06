package org.inventory_tracker.service;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateCompanyRequest;
import org.inventory_tracker.dto.response.CompanyResponse;
import org.inventory_tracker.entity.Company;
import org.inventory_tracker.repository.CompanyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public CompanyResponse create(CreateCompanyRequest request) {
        Company company = modelMapper.map(request, Company.class);
        Company savedCompany = companyRepository.save(company);
        return modelMapper.map(savedCompany, CompanyResponse.class);
    }

    public List<CompanyResponse> getAll() {

        List<Company> companies = companyRepository.findAll();

        return companies.stream()
                .map(company ->
                        modelMapper.map(company, CompanyResponse.class)
                )
                .toList();
    }
}
