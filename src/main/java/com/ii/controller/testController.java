package com.ii.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/test")
public class testController {

	@GetMapping("")
	public String home() {
		log.info("Test!!!!!!!!!!!!!!!!");
		return "Hello";
	}
	
}