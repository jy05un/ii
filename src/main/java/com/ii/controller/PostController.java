package com.ii.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ii.object.entity.Post;
import com.ii.object.model.DTO.GetPostResDTO;
import com.ii.object.model.common.Response;
import com.ii.repository.IPostRepository;
import com.ii.service.PostService;
import com.ii.utils.ResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/posts")
public class PostController {
	
	private final IPostRepository postRepository;
	
	private final PostService postService;

	@GetMapping("/{id}")
	public ResponseEntity<Response> getPost(@PathVariable("id") UUID id) {
		
		try {
			GetPostResDTO getPostResDTO = postService.getPost(id);
			return ResponseUtil.build(HttpStatus.OK, "Post returned", getPostResDTO);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
		}
		
	}
	
	@GetMapping("/test")
	public ResponseEntity<Response> test() {
		
		List<Post> posts = postRepository.findAll();
		return ResponseUtil.build(HttpStatus.OK, "", posts);
	}
}