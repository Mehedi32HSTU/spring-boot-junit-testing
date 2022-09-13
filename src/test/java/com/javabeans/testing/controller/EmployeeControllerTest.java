package com.javabeans.testing.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javabeans.testing.model.Employee;
import com.javabeans.testing.repository.EmployeeRepository;
import com.javabeans.testing.service.AssistantEmployeeService;
import com.javabeans.testing.service.EmployeeService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EmployeeControllerTest {
	@MockBean
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmployeeService employeeService;

	@MockBean
	private AssistantEmployeeService assistantEmployeeService;

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	void getEmployeeByIdServiceTest() {
		// For the MocBean Only
		Employee createdEmployee = createTempEmployee(10L);
//		Long requiredEmployeeId = 4L;
		when(assistantEmployeeService.getMaxEmployeeId()).thenReturn(createdEmployee.getEmployeeId());
		when(employeeRepository.findByEmployeeId(any())).thenReturn(createTempEmployee(10L));

		assertAll(
				() -> assertEquals("Abc Def", ((Employee)employeeService.getEmployeeByEmployeeId(4L).getBody()).getEmployeeName()),
				() -> assertEquals(10L, ((Employee)employeeService.getEmployeeByEmployeeId(4L).getBody()).getEmployeeId()),
				() -> assertEquals(HttpStatus.OK, employeeService.getEmployeeByEmployeeId(4L).getStatusCode())

		);
	}
	
	@Test
	void addEmployeeServiceTest() {
		Employee createdEmployee = createTempEmployee(10L);
		when(employeeRepository.save(any())).thenReturn(createTempEmployee(6L));
		assertEquals(HttpStatus.OK, employeeService.createEmployee(createdEmployee).getStatusCode());

	}
	
	@Test
	void createEmployeeControllerTest() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String createdEmployeeJson = objectMapper.writeValueAsString(createTempEmployee(10L));
		when(employeeRepository.save(any())).thenReturn(createTempEmployee(6L));
		
		this.mockMvc.perform(post("/employee/add").contentType(MediaType.APPLICATION_JSON)
			.content(createdEmployeeJson)).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.employeeId").value(6L));
	}
	
	@Test
	void getAllEmployeeByPhoneControllerTest() throws Exception {
		List<Employee> allEmployees = new ArrayList<>();
		allEmployees.add(createTempEmployee(11L));
		allEmployees.add(createTempEmployee(12L));
		allEmployees.add(createTempEmployee(13L));
		allEmployees.add(createTempEmployee(14L));
		when(employeeRepository.findAllByEmployeePhone(any())).thenReturn(allEmployees);
		this.mockMvc.perform(get("/employee/").param("phone-number", "123456789")).andDo(print())
			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.length()").is(3))
			.andExpect(jsonPath("$.[0].employeeId").value(11L))
			.andExpect(jsonPath("$.[1].employeeId").value(12L))
			.andExpect(jsonPath("$.[2].employeeId").value(13L));
	}

	@Test
	void getEmployeeByEmployeeIdControllerTest() throws Exception {
		Long employeeId = 12L;
		when(employeeRepository.findByEmployeeId(any())).thenReturn(createTempEmployee(employeeId));
		
		this.mockMvc.perform(get("/employee/{employeeid}",1)).andDo(print())
		.andExpect(status().isOk())
//			.andExpect(jsonPath("$.length()").is(3))
		.andExpect(jsonPath("$.employeeId").value(12L));
	}
	
	@Test
	void updateEmployeeControllerTest() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		Long employeeId = 7L;
		String createdEmployeeJson = objectMapper.writeValueAsString(createTempEmployee(employeeId));
		when(employeeRepository.save(any())).thenReturn(createTempEmployee(7L));
		when(employeeRepository.findByEmployeeId(any())).thenReturn(createTempEmployee(10L));
		this.mockMvc.perform(put("/employee/{employeeId}/update",employeeId).contentType(MediaType.APPLICATION_JSON)
				.content(createdEmployeeJson))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.employeeId").value(7L))
//				.andExpect(MockMvcResultMatchers.content().json(createdEmployeeJson))
				.andExpect(MockMvcResultMatchers.content().json("{\r\n"
						+ "        \"employeeId\": 7,\r\n"
						+ "        \"employeeName\": \"Abc Def\",\r\n"
						+ "        \"employeeEmail\": \"nsbfjhgs@kjdhkh.com\",\r\n"
						+ "        \"employeePhone\": \"24786562468\",\r\n"
						+ "        \"employeeAddress\": \"asdfghj\"\r\n"
						+ "    }"));
	}
	
	@Test
	void deleteEmployeeControllerTest() throws Exception {
		Long employeeId = 3L;
		when(employeeRepository.existsByEmployeeId(any())).thenReturn(true);
		doNothing().when(employeeRepository).delete(any()); // this will say the repository to do nothing when delete is called. 
															//	we can also test delete method if we only mock the repository.  
		this.mockMvc.perform(delete("/employee/{employeeId}/delete",employeeId)).andDo(print())
		.andExpect(status().isOk());
		
	}

	public Employee createTempEmployee(Long employeeId) {
		Employee employee = new Employee();
		employee.setEmployeeId(employeeId);
//		Long employeeIdUpdated = assistantEmployeeService.getMaxEmployeeId();
		employee.setEmployeeName("Abc Def");
		employee.setEmployeeAddress("asdfghj");
		employee.setEmployeeEmail("nsbfjhgs@kjdhkh.com");
		employee.setEmployeePhone("24786562468");
		return employee;
	}
	

}
