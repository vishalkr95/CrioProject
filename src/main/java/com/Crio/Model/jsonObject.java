package com.Crio.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class jsonObject {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(length = 1000)
	@JsonProperty("UserDetails")
	String json;
	
	@Column(unique = true)
    @NotBlank(message = "Email can't be empty!")
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message = "Invalid email !!")
    private String email;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJson() {
		return json;
	}


	@Override
	public String toString() {
		return "jsonObject [id=" + id + ", json=" + json + "]";
	}

	public void setJson(String json) {
		this.json = json;
	}

}
