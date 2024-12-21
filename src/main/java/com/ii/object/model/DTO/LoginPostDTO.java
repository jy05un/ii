package com.ii.object.model.DTO;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginPostDTO {

	@NotEmpty
	@Size(min=5, max=32)
	private String username;
	@NotEmpty
	@Size(min=8, max=32)
	private String password;
}
