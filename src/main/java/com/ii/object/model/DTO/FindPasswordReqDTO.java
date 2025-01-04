package com.ii.object.model.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
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
public class FindPasswordReqDTO {

	@Column(unique = true)
	@NotNull
	@Size(min=5, max=40)
	private String username;
	
}
