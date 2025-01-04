package com.ii.object.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class RegisterReqDTO {

	@NotBlank
	@Size(min=5, max=32)
	@Pattern(regexp = "^[a-zA-Z0-9_-]*{5,32}$", message = "유저네임은 최소 5자, 최대 32자의 영문 대문자, 소문자, 숫자, 특수문자 -, _만 사용 가능합니다.")
	private String username;
	
	@NotBlank
	@Size(min=8, max=32)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$", message = "비밀번호는 최소 8자, 최대 32자의 대문자, 소문자, 숫자, 특수 문자를 포함해야 합니다.")
	private String password;
	
	@NotBlank
	@Email
	@Size(min=8, max=320)
	@Pattern(regexp = ".+@.+\\..+", message = "Email must contain a valid domain")
	private String email;
	
	@NotEmpty
	@Size(min=2, max=16)
	private String nickname;
	
}
