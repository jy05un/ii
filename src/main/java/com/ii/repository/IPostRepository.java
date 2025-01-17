package com.ii.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.Post;
import com.ii.object.model.enums.PostType;

public interface IPostRepository extends JpaRepository<Post, UUID> {

	List<Post> findAllByOrderByUploadedAtDesc(Pageable pageable);

	List<Post> findByUploadedAtLessThanOrderByUploadedAtDesc(LocalDateTime uploadedAt, Pageable pageable);

	List<Post> findByTypeInOrderByUploadedAtDesc(ArrayList<PostType> postTypeList, Pageable pageable);

	List<Post> findByUploadedAtLessThanAndTypeInOrderByUploadedAtDesc(LocalDateTime uploadedAt,
			ArrayList<PostType> postTypeList, Pageable pageable);
	
}
