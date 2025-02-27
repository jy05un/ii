package com.ii.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ii.object.entity.Base;
import com.ii.object.entity.Post;
import com.ii.object.model.DTO.GetFileResDTO;
import com.ii.object.model.DTO.GetPostResDTO;
import com.ii.object.model.DTO.GetPostsResDTO;
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
		
		Object data = post.getPostObject();
		List<GetFileResDTO> files = post.getFiles().stream().map(file -> {
			return new GetFileResDTO(file.getId(), file.getName(), file.getMimeType(), file.getSize(), file.getUrl(), file.getFileType());
		}).collect(Collectors.toList());
		
		return new GetPostResDTO(post.getId(), post.getType(), post.getStreamer(), files, data);
		
	}
	
	public GetPostsResDTO getPosts(int count, UUID cursor, String postTypes) throws IllegalArgumentException {
		
		ArrayList<PostType> postTypeList = new ArrayList<PostType>();
		for(String postTypeString : postTypes.split(",")) {
			PostType postType = PostType.valueOf(postTypeString);
			postTypeList.add(postType);
		}
		
		Pageable pageable = PageRequest.of(0, count);
		List<Post> posts;
		if(cursor == null) {
			posts = postRepository.findByTypeInOrderByUploadedAtDesc(postTypeList, pageable);
		} else {
			Post cursorPost = postRepository.findById(cursor)
					.orElseThrow(() -> new IllegalArgumentException("No post with id " + cursor.toString()));
			posts = postRepository.findByUploadedAtLessThanAndTypeInOrderByUploadedAtDesc(cursorPost.getUploadedAt(), postTypeList, pageable);
		}
		
		return new GetPostsResDTO(posts.size(), cursor, postTypeList, posts.stream().map(post -> {
			Object data = post.getPostObject();
			List<GetFileResDTO> files = post.getFiles().stream().map(file -> {
				return new GetFileResDTO(file.getId(), file.getName(), file.getMimeType(), file.getSize(), file.getUrl(), file.getFileType());
			}).collect(Collectors.toList());
			return new GetPostResDTO(post.getId(), post.getType(), post.getStreamer(), files, data);
		}).collect(Collectors.toList()));
		
	}
	
}
