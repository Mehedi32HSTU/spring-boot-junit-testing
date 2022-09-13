package com.javabeans.testing.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javabeans.testing.repository.EmployeeRepository;

@Service
public class AssistantEmployeeServiceImpl implements AssistantEmployeeService {
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Override
	public Long getMaxEmployeeId() {
		try {
			LOGGER.info("<<<<<<<<<<---------- GET MAX EMPLOYEE ID ---------->>>>>>>>>>");
			Long maxEmployeeId = employeeRepository.findMaxEmployeeId();
			if(Objects.isNull(maxEmployeeId))
				maxEmployeeId = 0L;
			maxEmployeeId++;
			return maxEmployeeId;

		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.info("Exception : {} has occured.",e.getMessage());
			return 1L;
		}
	}

}
