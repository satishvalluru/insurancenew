package com.java.service.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java.client.BankServiceClient;
import com.java.dto.CustomerPolicyDTO;
import com.java.dto.InsurancesDTO;
import com.java.dto.TransactionMasterRequest;
import com.java.model.Customer;
import com.java.model.CustomerPolicy;
import com.java.model.Insurances;
import com.java.repository.CustomerPolicyRepository;
import com.java.repository.CustomerRepository;
import com.java.repository.InsuranceTypeRepository;
import com.java.service.CustomerPolicyService;

@Service
public class CustomerPolicyServiceImpl implements CustomerPolicyService {

	private static final String Remarks = "insurance amount";

	@Autowired
	private CustomerRepository custRepository;

	@Autowired
	private CustomerPolicyRepository custPolicyRepository;

	@Autowired
	private InsuranceTypeRepository typeRepository;

	@Autowired
	private BankServiceClient bankClient;

	@Override
	public String optUserPolicy(CustomerPolicyDTO cusPolicyDTO) throws ParseException {
		CustomerPolicy customerPolicy = new CustomerPolicy();
		double total = 0;
		// int id =0;

		Optional<Customer> customer = custRepository.findById(cusPolicyDTO.getCustomerId());

		if (!customer.isPresent()) {
			return null;
		}

		Map<Long, Double> insurancePaymentMap = new HashMap<>();
		Map<Long, Double> insurancePaymentResult = new HashMap<>();
		// InsurancesDTO insurnceDto = new InsurancesDTO();

		for (InsurancesDTO insuranceDto : cusPolicyDTO.getInsuranceDTO()) {
			CustomerPolicy customerpolicy = new CustomerPolicy();

			customerpolicy.setCustomerId(customer.get().getCustomerId());
			customerpolicy.setDateTime(Calendar.getInstance().getTime());

			Optional<Insurances> insurance = typeRepository.findById(insuranceDto.getInsuranceId());
			Insurances insurances = insurance.get();

			total = insuranceDto.getAmount();

			insurancePaymentResult = insurancePayment(insurances, insurancePaymentMap, insuranceDto.getAmount());

			customerpolicy.setInsurance(insurance.get());
			customerpolicy.setPremiumAmount(total);

			customerpolicy.setStatus("Success");
			custPolicyRepository.save(customerpolicy);

		}

		if (total == 0) {
			return null;
		}

		customerPolicy.setPremiumAmount(total);
		// customerPolicy.getInsurance().setInsuranceId(id);

		String status = "";
		TransactionMasterRequest fundRequest = new TransactionMasterRequest();
		fundRequest.setFromAccount(cusPolicyDTO.getUserAccountNumber());
		fundRequest.setRemarks(Remarks);

		for (Map.Entry<Long, Double> entrySet : insurancePaymentResult.entrySet()) {
			fundRequest.setToAccount(entrySet.getKey());
			fundRequest.setAmount(entrySet.getValue());
			status = bankClient.fundTransfer(fundRequest);
		}

		return "Success";
	}

	private Map<Long, Double> insurancePayment(Insurances insurances, Map<Long, Double> insurancePaymentMap,
			double amount) {
		if (insurancePaymentMap.size() > 0) {
			Iterator<Long> iterator = insurancePaymentMap.keySet().iterator();

			while (iterator.hasNext()) {
				Long key = iterator.next();
				Double value = insurancePaymentMap.get(key);
				if (key.equals(insurances.getInsurcompany().getCompanyAccountNumber())) {
					insurancePaymentMap.replace(key, value + Double.valueOf(amount));
				} else {
					insurancePaymentMap.put(insurances.getInsurcompany().getCompanyAccountNumber(),
							Double.valueOf(amount));
				}
			}
		}

		else {
			insurancePaymentMap.put(insurances.getInsurcompany().getCompanyAccountNumber(), Double.valueOf(amount));
		}

		return insurancePaymentMap;
	}

	@Override
	public List<CustomerPolicy> getCustomerById(int customerId) {
		return custPolicyRepository.findByCustomerId(customerId);
	}

}