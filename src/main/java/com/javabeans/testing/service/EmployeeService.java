package com.javabeans.testing.service;

import org.springframework.http.ResponseEntity;

import com.javabeans.testing.model.Employee;

public interface EmployeeService {

	public ResponseEntity<?> createEmployee(Employee employee);
	public ResponseEntity<?> getEmployeeByEmployeeId(Long employeeId);
	public ResponseEntity<?> getEmployeeByPhone(String phoneNumber);	
	public ResponseEntity<?> getAllEmployee();
	public ResponseEntity<?> updateEmployeeBYEmployeeId(Long employeeId, Employee employeeRequest);
	public ResponseEntity<?> deleteByEmployeeId(Long employeeId);
	
}
