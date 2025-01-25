package com.ii.controller;

import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.hibernate.validator.constraints.Range;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ii.object.model.DTO.GetBookmarkResDTO;
import com.ii.object.model.DTO.GetBookmarksResDTO;
import com.ii.object.model.DTO.LoginReqDTO;
import com.ii.object.model.DTO.PostBookmarkPostReqDTO;
import com.ii.object.model.DTO.PostBookmarkReqDTO;
import com.ii.object.model.DTO.PostBookmarkResDTO;
import com.ii.object.model.DTO.PutBookmarkReqDTO;
import com.ii.object.model.common.Response;
import com.ii.object.model.common.exception.DataNotFoundException;
import com.ii.repository.IUserRepository;
import com.ii.service.BookmarkService;
import com.ii.service.UserService;
import com.ii.utils.ResponseUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {
	
	private final BookmarkService bookmarkService;
	
	@PostMapping("")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> addBookmark(@RequestBody @Valid PostBookmarkReqDTO postBookmarkReqDTO) {
		
		try {
			PostBookmarkResDTO postBookmarkResDTO = bookmarkService.addBookmark(postBookmarkReqDTO);
			return ResponseUtil.build(HttpStatus.CREATED, "bookmark created", postBookmarkResDTO);
		} catch (DataIntegrityViolationException e) {
			return ResponseUtil.build(HttpStatus.BAD_REQUEST, "같은 이름의 북마크가 이미 존재합니다!", null);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다!", null);
		}
		
	}
	
	@GetMapping("")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> getBookmarks() {
		
		try {
			GetBookmarksResDTO getBookmarksResDTO = bookmarkService.getBookmarks();
			return ResponseUtil.build(HttpStatus.OK, "bookmarks data sent!", getBookmarksResDTO);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다!", null);
		}
		
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> getBookmark(
			@PathVariable("id") UUID bookmarkId,
			@Range(min = 1, max = 20) @RequestParam(name = "count", defaultValue = "5") Integer count,
			@RequestParam(name = "cursor", required = false) UUID cursor
			) {
		
		try {
			GetBookmarkResDTO getBookmarkResDTO = bookmarkService.getBookmark(bookmarkId, count, cursor);
			return ResponseUtil.build(HttpStatus.OK, "bookmark data sent!", getBookmarkResDTO);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.UNAUTHORIZED, e.getMessage(), null);
		} catch (DataNotFoundException e) {
			return ResponseUtil.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
		}
		
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> updateBookmark(
			@PathVariable("id") UUID bookmarkId,
			@RequestBody @Valid PutBookmarkReqDTO putBookmarkReqDTO
			) {
		
		try {
			bookmarkService.updateBookmark(bookmarkId, putBookmarkReqDTO);
			return ResponseUtil.build(HttpStatus.NO_CONTENT, "bookmark name updated!", null);
		} catch (DataNotFoundException e) {
			return ResponseUtil.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다!", null);
		}
		
	}
	
	@PostMapping("/{id}/posts")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> addBookmarkPost(
			@PathVariable("id") UUID bookmarkId,
			@RequestBody @Valid PostBookmarkPostReqDTO postBookmarkPostReqDTO
			) {
		
		try {
			bookmarkService.addBookmarkPost(bookmarkId, postBookmarkPostReqDTO);
			return ResponseUtil.build(HttpStatus.CREATED, "post added to bookmark!", null);
		} catch (DataNotFoundException e) {
			return ResponseUtil.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다!", null);
		}
	
	}
	
	@DeleteMapping("/{bookmark_id}/posts/{post_id}")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Response> deleteBookmarkPost(
			@PathVariable("bookmark_id") UUID bookmarkId, 
			@PathVariable("post_id") UUID postId
			) {
		
		try {
			bookmarkService.deleteBookmarkPost(bookmarkId, postId);
			return ResponseUtil.build(HttpStatus.NO_CONTENT, "post deleted from bookmark!", null);
		} catch (DataNotFoundException e) {
			return ResponseUtil.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
		} catch (IllegalArgumentException e) {
			return ResponseUtil.build(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다!", null);
		}
	
	}
	
}
