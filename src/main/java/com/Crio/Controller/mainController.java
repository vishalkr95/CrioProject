package com.Crio.Controller;

import com.Crio.Model.Employee;
import com.Crio.Reposiroty.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class mainController {
	
    @Autowired
    private EmployeeRepository employeeRepository;
   
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PostMapping
    public String createEmployee(@RequestBody Employee employee) {
    	String emp=employee.toJson();
    	System.out.print(emp);
    	Employee emp1=new Employee();
    	emp1=emp1.fromJson(emp);
    	System.out.println(emp1);
    	Employee e=employeeRepository.save(employee);
        return  emp;
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
    	
    	 Optional<Employee> employeeOptional = employeeRepository.findById(id);
         return employeeOptional.orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));
               
    }

    
    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));

        employee.setName(employeeDetails.getName());
        employee.setDesignation(employeeDetails.getDesignation());
        employee.setSalary(employeeDetails.getSalary());

        return employeeRepository.save(employee);
    }

    
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }

    

    
}

