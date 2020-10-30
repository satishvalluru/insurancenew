package com.java.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.model.InsuranceCompanies;
import com.java.repository.InsuranceCompanyRepository;
import com.java.service.InsuranceCompaniesService;

@Service
public class InsuranceCompaniesServiceImpl implements InsuranceCompaniesService {

	@Autowired
	private InsuranceCompanyRepository companyRepository;
	
	@Override
	public List<InsuranceCompanies> listcompanies() {
		return companyRepository.findAll();
	}
	
	

}
