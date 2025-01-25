package com.ii.object.entity;

import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "refresh_token")
public class RefreshToken extends Base {
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	@Size(max = 512)
	private String token;
	
	@Column(name = "device_id")
	@Builder.Default
	private UUID deviceId = UUID.randomUUID();
	
}
