package com.ii.object.entity;

import java.sql.Date;
import java.util.UUID;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.sql.FalseCondition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users") // user가 예약어라 안됨 엣큥
public class User extends Base {

	@Id
	@Builder.Default
	private UUID id = UUID.randomUUID();
	
	@Column(unique = true)
	@NotNull
	@Size(min=5, max=32)
	private String username;
	
	@Column(name = "hashed_password")
	@NotNull
	private String hashedPassword;
	
	@Column(unique = true)
	@NotNull
	@Email
	private String email;
	
	@Builder.Default
	private String role = "user";
	
	@Column(name = "mail_auth")
	@Builder.Default
	private Boolean mailAuth = true;
	
}
