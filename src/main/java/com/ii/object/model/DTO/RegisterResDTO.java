package com.ii.object.model.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegisterResDTO {

	private UUID id;
	private String username;
	
}
