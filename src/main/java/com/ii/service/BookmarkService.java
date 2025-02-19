package com.ii.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ii.object.entity.Bookmark;
import com.ii.object.entity.BookmarkPost;
import com.ii.object.entity.Post;
import com.ii.object.entity.User;
import com.ii.object.model.DTO.BookmarkSummary;
import com.ii.object.model.DTO.GetBookmarkResDTO;
import com.ii.object.model.DTO.GetBookmarksResDTO;
import com.ii.object.model.DTO.GetFileResDTO;
import com.ii.object.model.DTO.GetPostResDTO;
import com.ii.object.model.DTO.PostBookmarkPostReqDTO;
import com.ii.object.model.DTO.PostBookmarkReqDTO;
import com.ii.object.model.DTO.PostBookmarkResDTO;
import com.ii.object.model.DTO.PutBookmarkReqDTO;
import com.ii.object.model.common.exception.DataNotFoundException;
import com.ii.repository.IBookmarkPostRepository;
import com.ii.repository.IBookmarkRepository;
import com.ii.repository.IPostRepository;
import com.ii.repository.IUserRepository;
import com.ii.utils.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
	
	private final IUserRepository userRepository;
	private final IBookmarkRepository bookmarkRepository;
	private final IBookmarkPostRepository bookmarkPostRepository;
	private final IPostRepository postRepository;
	
	public PostBookmarkResDTO addBookmark(PostBookmarkReqDTO postBookmarkReqDTO) throws IllegalArgumentException, DataIntegrityViolationException {
		
		User me = userRepository.findByUsername(SecurityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("로그인 정보 없는데 어캐 접근했음?")));
		
		if(me.getBookmarks().size() >= 10) throw new IllegalArgumentException("최대 10개의 북마크를 생성할 수 있습니다!");
		
		Bookmark bookmark = Bookmark.builder()
				.name(postBookmarkReqDTO.getBookmarkName())
				.user(me)
				.build();
		bookmarkRepository.save(bookmark);
		return new PostBookmarkResDTO(bookmark.getId(), bookmark.getName());
		
	}
	
	public GetBookmarksResDTO getBookmarks() throws IllegalArgumentException {
		
		User me = userRepository.findByUsername(SecurityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("로그인 정보 없는데 어캐 접근했음?")));
		
		List<Bookmark> bookmarks = me.getBookmarks();
		
		List<BookmarkSummary> bookmarkSummaries = new ArrayList<BookmarkSummary>();
		for(Bookmark bookmark : bookmarks) {
			GetPostResDTO getPostResDTO;
			if(bookmark.getPosts().size() == 0) getPostResDTO = null;
			else {
				Post post = bookmark.getPosts().getLast().getPost();
				List<GetFileResDTO> files = post.getFiles().stream().map(file -> {
					return new GetFileResDTO(file.getId(), file.getName(), file.getMimeType(), file.getSize(), file.getUrl(), file.getFileType());
				}).collect(Collectors.toList());
				getPostResDTO = new GetPostResDTO(post.getId(), post.getType(), post.getStreamer(), files, post.getPostObject());
			}
			bookmarkSummaries.add(new BookmarkSummary(bookmark.getId(), bookmark.getName(), bookmark.getPosts().size(), getPostResDTO));
		}
		
		return new GetBookmarksResDTO(bookmarkSummaries, bookmarkSummaries.size());
		
	}
	
	public void addBookmarkPost(UUID bookmarkId, PostBookmarkPostReqDTO postBookmarkPostReqDTO) throws DataNotFoundException, IllegalArgumentException {
		
		String username = SecurityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("인증 정보가 존재하지 않습니다!"));
		Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
				.orElseThrow(() -> new DataNotFoundException("Bookmark Not Found"));
		if(!bookmark.getUser().getUsername().equals(username)) throw new IllegalArgumentException("Can't access to this bookmark");
		
		UUID postId = postBookmarkPostReqDTO.getPostId();
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new DataNotFoundException("Post Not Found"));
		BookmarkPost bookmarkPost = BookmarkPost.builder()
				.post(post)
				.bookmark(bookmark)
				.build();
		bookmarkPostRepository.save(bookmarkPost);
		
	}
	
	public GetBookmarkResDTO getBookmark(UUID bookmarkId, int count, UUID cursor) throws DataNotFoundException, IllegalArgumentException {
		
		String username = SecurityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("인증 정보가 존재하지 않습니다!"));
		Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
				.orElseThrow(() -> new DataNotFoundException("Bookmark Not Found"));
		if(!bookmark.getUser().getUsername().equals(username)) throw new IllegalArgumentException("Can't access to this bookmark");
		
		Pageable pageable = PageRequest.of(0, count);
		
		List<BookmarkPost> bookmarkPosts;
		if(cursor == null) bookmarkPosts = bookmarkPostRepository.findAllByOrderByCreatedAtDesc(pageable);
		else {
			BookmarkPost bookmarkPost = bookmarkPostRepository.findByPostId(cursor)
					.orElseThrow(() -> new DataNotFoundException("Cursor data not found!"));
			bookmarkPosts = bookmarkPostRepository.findByCreatedAtLessThanOrderByCreatedAtDesc(bookmarkPost.getCreatedAt(), pageable);
		}
		
		List<GetPostResDTO> posts = new ArrayList<GetPostResDTO>();
		for(BookmarkPost bookmarkPost : bookmarkPosts) {
			Post post = bookmarkPost.getPost();
			List<GetFileResDTO> files = post.getFiles().stream().map(file -> {
				return new GetFileResDTO(file.getId(), file.getName(), file.getMimeType(), file.getSize(), file.getUrl(), file.getFileType());
			}).collect(Collectors.toList());
			posts.add(new GetPostResDTO(post.getId(), post.getType(), post.getStreamer(), files, post.getPostObject()));
		}
		
		return new GetBookmarkResDTO(bookmark.getId(), bookmark.getName(), posts.size(), posts);
	}
	
	public void updateBookmark(UUID bookmarkId, PutBookmarkReqDTO putBookmarkReqDTO) throws DataNotFoundException, IllegalArgumentException {
		
		String username = SecurityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("인증 정보가 존재하지 않습니다!"));
		Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
				.orElseThrow(() -> new DataNotFoundException("Bookmark Not Found"));
		if(!bookmark.getUser().getUsername().equals(username)) throw new IllegalArgumentException("Can't access to this bookmark");
		
		bookmark.setName(putBookmarkReqDTO.getBookmarkName());
		bookmarkRepository.save(bookmark);
	}
	
	public void deleteBookmarkPost(UUID bookmarkId, UUID postId) throws DataNotFoundException, IllegalArgumentException {
		
		String username = SecurityUtil.getCurrentUsername()
				.orElseThrow(() -> new IllegalArgumentException("인증 정보가 존재하지 않습니다!"));
		Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
				.orElseThrow(() -> new DataNotFoundException("Bookmark Not Found"));
		if(!bookmark.getUser().getUsername().equals(username)) throw new IllegalArgumentException("Can't access to this bookmark");
		
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new DataNotFoundException("Post Not Found"));
		BookmarkPost bookmarkPost = bookmarkPostRepository.findByBookmarkIdAndPostId(bookmarkId, postId)
				.orElseThrow(() -> new DataNotFoundException("해당 북마크에 등록한 적이 없습니다!"));
		bookmarkPostRepository.delete(bookmarkPost);
		
	}

}
