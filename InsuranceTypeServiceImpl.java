package com.java.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.dto.InsuranceTypeDTO;
import com.java.model.Insurances;
import com.java.repository.InsuranceTypeRepository;
import com.java.service.InsuranceTypeService;

@Service
public class InsuranceTypeServiceImpl implements InsuranceTypeService {

	@Autowired
	private InsuranceTypeRepository typeRepository;

	@Override
	public List<InsuranceTypeDTO> searchInsuranceName(String insuranceName) {
		List<Insurances> insuranceType = typeRepository.findByInsuranceNameContains(insuranceName);
		List<InsuranceTypeDTO> insuranceTypeDtos = new ArrayList<>();
		InsuranceTypeDTO insuranceDto = null;
		for(Insurances insuranceDtos : insuranceType) {
			insuranceDto = new InsuranceTypeDTO();
			BeanUtils.copyProperties(insuranceDtos, insuranceDto);
			insuranceTypeDtos.add(insuranceDto);
		}
		return insuranceTypeDtos;
	}
}
