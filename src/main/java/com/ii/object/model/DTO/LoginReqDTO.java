package com.ii.object.model.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginReqDTO {

	@NotEmpty
	@Size(min=5, max=32)
	private String username;
	@NotEmpty
	@Size(min=8, max=32)
	private String password;
}
