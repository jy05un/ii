package com.ii.object.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@Data
public class GetIsUsernameExistResDTO {
	
	Boolean exists;

}
