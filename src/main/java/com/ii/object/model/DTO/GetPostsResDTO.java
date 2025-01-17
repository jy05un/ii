package com.ii.object.model.DTO;

import java.util.List;
import java.util.UUID;

import com.ii.object.model.enums.PostType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Data
public class GetPostsResDTO {

	private final int count;
	private UUID after;
	private List<PostType> postTypes;
	private List<GetPostResDTO> posts;
	
}
