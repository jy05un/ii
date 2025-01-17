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
public class UpdateUserNicknameReqDTO {
	
	@NotEmpty
	@Size(min=2, max=16)
	private String nickname;

}
