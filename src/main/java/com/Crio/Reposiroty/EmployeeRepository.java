package com.Crio.Reposiroty;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Crio.Model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}