package com.ii.object.model.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class GetBookmarkResDTO {

	private UUID id;
	private String name;
	private int size;
	private List<GetPostResDTO> posts = new ArrayList<GetPostResDTO>();
	
}

