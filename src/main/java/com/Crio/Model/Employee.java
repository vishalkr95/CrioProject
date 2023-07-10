package com.Crio.Model;

import java.io.IOException;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("employee_name")
    private String name;

    @JsonProperty("employee_designation") 
    private String designation;

    @JsonProperty("employee_salary") 
    private double salary;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}


    public  String toJson() {
        try {
        	ObjectMapper ObjectMapper=new ObjectMapper();
            String ans= ObjectMapper.writeValueAsString(this);
            return ans;
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing Employee to JSON", e);
        }
    }

    public Employee fromJson(String json) {
        try {
        	ObjectMapper ObjectMapper=new ObjectMapper();
            return ObjectMapper.readValue(json, Employee.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing Employee from JSON", e);
        }
    }
}
