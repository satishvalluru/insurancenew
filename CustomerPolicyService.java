package com.java.service;

import java.text.ParseException;
import java.util.List;

import com.java.dto.CustomerPolicyDTO;
import com.java.model.CustomerPolicy;

public interface CustomerPolicyService {

	public List<CustomerPolicy> getCustomerById(int customerId);

	String optUserPolicy(CustomerPolicyDTO cusPolicyDTO) throws ParseException;

}
