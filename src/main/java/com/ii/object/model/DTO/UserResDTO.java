package com.ii.object.model.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UserResDTO {

	private UUID id;
	
	private String username;
	
	private String email;
	
	private String role;
	
}
