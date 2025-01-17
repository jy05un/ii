package com.ii.service;

import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ii.object.entity.Post;
import com.ii.object.model.DTO.GetPostResDTO;
import com.ii.object.model.enums.PostType;
import com.ii.repository.IPostRepository;
import com.ii.utils.AsyncMailSender;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	
	private final IPostRepository postRepository;

	public GetPostResDTO getPost(UUID id) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No post with id " + id.toString()));
		
		Object data = switch (post.getType()) {
			case PostType.Cafe		-> post.getCafePost();
			case PostType.Soop		-> post.getSoopPost();
			case PostType.X 		-> post.getXPost();
			case PostType.Instagram	-> post.getIgPost();
			default 				-> throw new IllegalArgumentException("Unexpected value: " + post.getType());
		};
		
		return new GetPostResDTO(post.getId(), post.getType(), post.getStreamer(), data);
		
	}
	
}
