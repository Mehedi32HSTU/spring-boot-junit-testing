package com.javabeans.testing.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.catalina.authenticator.SavedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.javabeans.testing.model.Employee;
import com.javabeans.testing.repository.EmployeeRepository;

@Service
public class EmployeeServiceImplementation implements EmployeeService {

	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private AssistantEmployeeService assistantEmployeeService;

	@Override
	public ResponseEntity<?> createEmployee(Employee employee) {
		try {
			LOGGER.info("<<<<<<<<<<---------- createEmployee ---------->>>>>>>>>>");
			System.out.println("Before Creation : "+ employee);

			Employee savedEmployee = employeeRepository.save(employee);
			System.out.println("Employee After Save : "+savedEmployee);
			return ResponseEntity.ok().body(savedEmployee);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.info("Exception : {} has occured.",e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception : "+ e.getMessage()+" has occured.");
		}
	}

	@Override
	public ResponseEntity<?> getEmployeeByEmployeeId(Long employeeId) {
		try {
			LOGGER.info("<<<<<<<<<<---------- getEmployeeByEmployeeId ---------->>>>>>>>>>");
			Employee employee = employeeRepository.findByEmployeeId(employeeId);
			if(Objects.isNull(employee))
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with Id : "+ employeeId +" not found.");
//			Long newEmployeeId = assistantEmployeeService.getMaxEmployeeId();
//			System.out.println("New Employee Id : "+newEmployeeId);
			
			return ResponseEntity.ok().body(employee);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.info("Exception : {} has occured.",e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception : "+ e.getMessage()+" has occured.");
		}
	}

	@Override
	public ResponseEntity<?> getAllEmployee() {
		try {
			LOGGER.info("<<<<<<<<<<---------- getAllEmployee ---------->>>>>>>>>>");
			List<Employee> allEmployees = employeeRepository.findAll();
			return ResponseEntity.ok().body(allEmployees);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.info("Exception : {} has occured.",e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception : "+ e.getMessage()+" has occured.");
		}
	}

	@Override
	public ResponseEntity<?> updateEmployeeBYEmployeeId(Long employeeId, Employee employeeRequest) {
		try {
			LOGGER.info("<<<<<<<<<<---------- updateEmployeeBYEmployeeId ---------->>>>>>>>>>");
			Employee employee = employeeRepository.findByEmployeeId(employeeId);
			if(Objects.isNull(employee))
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with Id : "+ employeeId +" not found.");
			employee.setEmployeeName(employeeRequest.getEmployeeName());
			employee.setEmployeeEmail(employeeRequest.getEmployeeEmail());
			employee.setEmployeePhone(employeeRequest.getEmployeePhone());
			employee.setEmployeeAddress(employeeRequest.getEmployeeAddress());

			Employee savedEmployee = employeeRepository.save(employee);
			return ResponseEntity.ok().body(savedEmployee);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.info("Exception : {} has occured.",e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception : "+ e.getMessage()+" has occured.");
		}
	}

	@Override
	public ResponseEntity<?> deleteByEmployeeId(Long employeeId) {
		try {
			LOGGER.info("<<<<<<<<<<---------- deleteByEmployeeId ---------->>>>>>>>>>");
			Boolean isEmployeeExists = employeeRepository.existsByEmployeeId(employeeId);
			if(!isEmployeeExists)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with Id : "+ employeeId +" not found.");
			employeeRepository.deleteById(employeeId);
			
			return ResponseEntity.ok().body("Employee with Id : "+employeeId+" deleted successfully.");
			
//			if(isDeleted)
//				return ResponseEntity.ok().body("Employee with Id : "+employeeId+" deleted successfully.");
//			else
//				return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Failed to delete employee with Id : "+ employeeId);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.info("Exception : {} has occured.",e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception : "+ e.getMessage()+" has occured.");
		}
	}

	@Override
	public ResponseEntity<?> getEmployeeByPhone(String phoneNumber) {
		try {
			LOGGER.info("<<<<<<<<<<---------- getEmployeeByPhone ---------->>>>>>>>>>");
			if(Objects.isNull(phoneNumber))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number can not be empty.");
			List<Employee> allEmployee = employeeRepository.findAllByEmployeePhone(phoneNumber);
			return ResponseEntity.ok().body(allEmployee);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.info("Exception : {} has occured.",e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception : "+ e.getMessage()+" has occured.");
		}
	}

}
