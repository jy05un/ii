package com.ii.object.model.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostBookmarkPostReqDTO {
	
	UUID postId;

}
