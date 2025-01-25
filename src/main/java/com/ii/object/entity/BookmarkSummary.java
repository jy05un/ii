package com.ii.object.entity;

import com.ii.object.model.DTO.GetPostResDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookmarkSummary {
	
	String name;
	Integer size;
	GetPostResDTO coverPost;
	
}