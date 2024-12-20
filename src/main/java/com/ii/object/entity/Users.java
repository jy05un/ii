package com.ii.object.entity;

import java.util.UUID;

import org.hibernate.annotations.BatchSize;
import org.springframework.data.relational.core.sql.FalseCondition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Entity
public class Users {

	@Id
	private UUID id;
	
	@Column(nullable = false)
	@Size(min=5, max=32)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	@Email
	private String email;
	
	@Column(nullable = false)
	private String role;
	
}
