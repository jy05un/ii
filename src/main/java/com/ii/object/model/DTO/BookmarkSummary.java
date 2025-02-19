package com.ii.object.model.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookmarkSummary {
	
	UUID id;
	String name;
	Integer size;
	GetPostResDTO coverPost;
	
}