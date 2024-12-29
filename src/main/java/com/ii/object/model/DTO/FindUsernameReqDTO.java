package com.ii.object.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class FindUsernameReqDTO {

	@NotBlank
	@Email
	@Size(min=8, max=320)
	@Pattern(regexp = ".+@.+\\..+", message = "Email must contain a valid domain")
	private String email;
	
}
