package com.ii.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ii.object.entity.Post;
import com.ii.object.model.common.Response;
import com.ii.repository.IPostRepository;
import com.ii.utils.ResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/test")
public class testController {
	
	private final IPostRepository postRepository;

	@GetMapping("")
	public String home() {
		log.info("Test!!!!!!!!!!!!!!!!");
		return "Hello";
	}
	
	@GetMapping("/test")
	public ResponseEntity<Response> test() {
		
		List<Post> posts = postRepository.findAll();
		return ResponseUtil.build(HttpStatus.OK, "", posts);
	}
}