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
public class UpdateEmailReqDTO {

	@NotBlank
	@Size(min=8, max=32)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$", message = "비밀번호는 최소 8자, 최대 32자의 대문자, 소문자, 숫자, 특수 문자를 포함해야 합니다.")
	private String password;
	
	@NotBlank
	@Email
	@Size(min=8, max=320)
	@Pattern(regexp = ".+@.+\\..+", message = "Email must contain a valid domain")
	private String email;
	
}
