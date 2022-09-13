package com.javabeans.testing;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javabeans.testing.model.Employee;

@Repository
public interface TestH2EmployeeRepository extends JpaRepository<Employee, Long>{
	Employee findByEmployeeId(Long employeeId);
	Boolean existsByEmployeeId(Long employeeId);
	List<Employee> findAllByEmployeePhone(String phoneNumber);
	@Query(value = "Select Max(employee.employee_id) from employee", nativeQuery = true)
	Long findMaxEmployeeId();
}
