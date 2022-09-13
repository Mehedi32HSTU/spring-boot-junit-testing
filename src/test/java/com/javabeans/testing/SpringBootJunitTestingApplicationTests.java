package com.javabeans.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.javabeans.testing.model.Employee;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SpringBootJunitTestingApplicationTests {

	@LocalServerPort
	private int port;

	private String baseUrl = "http://localhost:";
	private static RestTemplate restTemplate;

	@Autowired
	private TestH2EmployeeRepository testH2EmployeeRepository;

	@BeforeAll
	public static void initialize() {
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setBaseUrl() {
		baseUrl = baseUrl.concat(port+"").concat("/employee");
	}

	@Test
	@Sql(statements = "DELETE FROM EMPLOYEE", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void testAddEmployee() {
		baseUrl = baseUrl.concat("/add");
		Employee firstEmployee = restTemplate.postForObject(baseUrl, createTempEmployee(1L), Employee.class);
		Employee secondEmployee = restTemplate.postForObject(baseUrl, createTempEmployee(2L), Employee.class);
		Employee thirdEmployee = restTemplate.postForObject(baseUrl, createTempEmployee(3L), Employee.class);
		System.out.println("\n\nIn H2 Repo : "+testH2EmployeeRepository.findAll());
		
		assertAll(
			() -> assertEquals("Employee_"+1, firstEmployee.getEmployeeName(), ()-> "Employee id should be same as given."),
			() -> assertEquals(testH2EmployeeRepository.findById(3L).get().getEmployeePhone(), thirdEmployee.getEmployeePhone (), ()-> "Employee phone should be same as given."),
			() -> assertEquals(3, testH2EmployeeRepository.findAll().size(), ()-> "All employee size should be the number of entry that are in the table right now.")
		);
	}

	@Test
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (1, 'Employee_1', 'ADDRESS_OF_EMPLOYEE_1', 'Email_Of_Employee_1', '987654321_1')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (2, 'Employee_2', 'ADDRESS_OF_EMPLOYEE_2', 'Email_Of_Employee_2', '987654321_2')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (3, 'Employee_3', 'ADDRESS_OF_EMPLOYEE_3', 'Email_Of_Employee_3', '987654321_3')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (4, 'Employee_4', 'ADDRESS_OF_EMPLOYEE_4', 'Email_Of_Employee_4', '987654321_4')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM EMPLOYEE", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)	
	void getEmployeeById() {
		Long employeeId = 3L;
		baseUrl = baseUrl+"/"+employeeId;
		Employee employee = restTemplate.getForObject(baseUrl, Employee.class);
		System.out.println("Employee Found : "+employee);
		assertAll(
			() -> assertEquals("Employee_"+employeeId, employee.getEmployeeName()),
			() -> assertEquals("ADDRESS_OF_EMPLOYEE_"+employeeId, employee.getEmployeeAddress()),
			() -> assertEquals("Email_Of_Employee_"+employeeId, employee.getEmployeeEmail()),
			() -> assertEquals("987654321_"+employeeId, employee.getEmployeePhone())
		);
	}

	@Test
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (1, 'Employee_1', 'ADDRESS_OF_EMPLOYEE_1', 'Email_Of_Employee_1', '987654321_1')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (2, 'Employee_2', 'ADDRESS_OF_EMPLOYEE_2', 'Email_Of_Employee_2', '987654321_2')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (3, 'Employee_3', 'ADDRESS_OF_EMPLOYEE_3', 'Email_Of_Employee_3', '987654321_3')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (4, 'Employee_4', 'ADDRESS_OF_EMPLOYEE_4', 'Email_Of_Employee_4', '987654321_4')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM EMPLOYEE ", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void getAllEmployeeTest() {
		/*
		 * Special Cases: If we want to take a list of entity as response with the type ResponseEntity
		 * then we have to cale as exchange with the base URI, HttpMethod, request entity (if any)
		 * and the ParameterizedTypeReference (In the type we want to cast our response.)
		 * Ref: https://www.baeldung.com/spring-resttemplate-json-list
		 */
		baseUrl = baseUrl+"/all";
		ResponseEntity<List<Employee>> getAllEmployeeResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null,
																			new ParameterizedTypeReference<List<Employee>>() {});
		List<Employee> allEmployees = getAllEmployeeResponse.getBody();
		System.out.println("All employees : "+allEmployees);

		assertAll(
			() -> assertEquals(4, allEmployees.size()),
			() -> assertEquals(1L, ((Employee)allEmployees.get(0)).getEmployeeId()),
			() -> assertEquals("ADDRESS_OF_EMPLOYEE_3", ((Employee)allEmployees.get(2)).getEmployeeAddress())

		);		
	}

	@Test
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (1, 'Employee_1', 'ADDRESS_OF_EMPLOYEE_1', 'Email_Of_Employee_1', '987654321_1')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (2, 'Employee_2', 'ADDRESS_OF_EMPLOYEE_2', 'Email_Of_Employee_2', '987654321_2')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (3, 'Employee_3', 'ADDRESS_OF_EMPLOYEE_3', 'Email_Of_Employee_3', '987654321_3')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (4, 'Employee_4', 'ADDRESS_OF_EMPLOYEE_4', 'Email_Of_Employee_4', '987654321_4')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM EMPLOYEE ", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void updateEmployeeTest() {
		Long employeeId = 2L;
		baseUrl = baseUrl+"/"+employeeId+"/update";
		Employee employee = new Employee();
		employee.setEmployeeName("Employee_"+employeeId);
		employee.setEmployeeAddress("ADDRESS_OF_EMPLOYEE_"+employeeId);
		employee.setEmployeePhone("987654321_"+employeeId);
		employee.setEmployeeEmail("Email_Of_Employee_@"+employeeId); // updating email
		restTemplate.put(baseUrl, employee);
		Employee updatedEmployee = testH2EmployeeRepository.findByEmployeeId(employeeId);
		System.out.println("Updated Employee : "+updatedEmployee);

		assertAll(
			() -> assertEquals(employeeId, updatedEmployee.getEmployeeId()),
			() -> assertEquals("Employee_"+employeeId, updatedEmployee.getEmployeeName()),
			() -> assertEquals("ADDRESS_OF_EMPLOYEE_"+employeeId, updatedEmployee.getEmployeeAddress()),
			() -> assertEquals("Email_Of_Employee_@"+employeeId, updatedEmployee.getEmployeeEmail()),
			() -> assertEquals("987654321_"+employeeId, updatedEmployee.getEmployeePhone())
		);
	}

	@Test
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (1, 'Employee_1', 'ADDRESS_OF_EMPLOYEE_1', 'Email_Of_Employee_1', '987654321_1')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (2, 'Employee_2', 'ADDRESS_OF_EMPLOYEE_2', 'Email_Of_Employee_2', '987654321_2')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (3, 'Employee_3', 'ADDRESS_OF_EMPLOYEE_3', 'Email_Of_Employee_3', '987654321_3')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "INSERT INTO EMPLOYEE (employee_id, employee_name, employee_address, employee_email, employee_phone)"
			+ "VALUES (4, 'Employee_4', 'ADDRESS_OF_EMPLOYEE_4', 'Email_Of_Employee_4', '987654321_4')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM EMPLOYEE ", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void deleteEmployeeTest() {
		System.out.println("Before Delete : "+testH2EmployeeRepository.findAll());
		Long employeeId = 2L;
		baseUrl = baseUrl+"/"+employeeId+"/delete";
		restTemplate.delete(baseUrl);
		List<Employee> allEmployees = testH2EmployeeRepository.findAll();
		System.out.println("After Delete : "+allEmployees);
		assertAll(
			() -> assertEquals(3, allEmployees.size())
		);
	}

	public Employee createTempEmployee(Long employeeId) {
		Employee employee = new Employee();
		employee.setEmployeeId(employeeId);
		employee.setEmployeeName("Employee_"+employeeId);
		employee.setEmployeeAddress("ADDRESS_OF_EMPLOYEE_"+employeeId);
		employee.setEmployeeEmail("Email_Of_Employee_"+employeeId+"@mail.com");
		employee.setEmployeePhone("987654321_"+employeeId);
		return employee;
	}
}
