package com.ii.object.model.DTO;

import java.util.List;
import java.util.UUID;

import com.ii.object.model.enums.FileType;
import com.ii.object.model.enums.PostType;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetFileResDTO {
	
	private UUID id;
	private String name;
	private String mimeType;
	private Integer size;
	private String url;
	private FileType fileType;

}
