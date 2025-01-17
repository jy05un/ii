package com.ii.object.model.DTO;

import java.util.UUID;

import com.ii.object.entity.Streamer;
import com.ii.object.model.enums.PostType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetPostResDTO {
	
	private UUID id;
	private PostType type;
	private Streamer streamer;
	private Object data;
	

}
