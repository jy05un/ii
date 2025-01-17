package com.ii.controller;

import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ii.object.entity.Post;
import com.ii.object.model.DTO.GetPostResDTO;
import com.ii.object.model.DTO.GetPostsResDTO;
import com.ii.object.model.common.Response;
import com.ii.repository.IPostRepository;
import com.ii.service.PostService;
import com.ii.utils.ResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@RestController
@Validated
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
	
	@GetMapping("")
	public ResponseEntity<Response> getPosts(
			@Range(min = 1, max = 20) @RequestParam(name = "count", defaultValue = "5") Integer count,
			@RequestParam(name = "cursor", required = false) UUID cursor,
			@RequestParam(name = "post_types", defaultValue = "X,Soop,Instagram,Cafe") String postTypes
	) {
		try {
			GetPostsResDTO getPostsResDTO = postService.getPosts(count, cursor, postTypes);
			return ResponseUtil.build(HttpStatus.OK, "", getPostsResDTO);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
		}
	}
}