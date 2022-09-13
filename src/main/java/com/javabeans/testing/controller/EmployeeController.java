package com.javabeans.testing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javabeans.testing.model.Employee;
import com.javabeans.testing.service.EmployeeService;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {
	@Autowired
	private EmployeeService employeeService;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<?> createEmployee(@RequestBody Employee employee){
		return employeeService.createEmployee(employee);
	}
	
	@RequestMapping(value = "/{employeeId}", method = RequestMethod.GET)
	public ResponseEntity<?> getEmployeeByEmployeeId(@PathVariable ("employeeId") Long employeeId){
		return employeeService.getEmployeeByEmployeeId(employeeId);
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<?> getAllEmployee(){
		return employeeService.getAllEmployee();
	}
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<?> getAllEmployeeByPhone(
			@RequestParam(name = "phone-number", required = true) String phoneNumber){
		return employeeService.getEmployeeByPhone(phoneNumber);
	}
	
	@RequestMapping(value = "/{employeeId}/update", method = RequestMethod.PUT)
	public ResponseEntity<?> updateEmployee(
			@PathVariable ("employeeId") Long employeeId,
			@RequestBody Employee employeeRequest){
		return employeeService.updateEmployeeBYEmployeeId(employeeId, employeeRequest);
	}
	
	@RequestMapping(value = "/{employeeId}/delete", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteEmployee(
			@PathVariable ("employeeId") Long employeeId ) {
		return employeeService.deleteByEmployeeId(employeeId);
	}

}
