package com.ii.repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.Post;
import com.ii.object.model.enums.PostType;

public interface IPostRepository extends JpaRepository<Post, UUID> {

	List<Post> findAllByOrderByUploadedAtDesc(Pageable pageable);

	List<Post> findByUploadedAtLessThanOrderByUploadedAtDesc(OffsetDateTime uploadedAt, Pageable pageable);

	List<Post> findByTypeInOrderByUploadedAtDesc(ArrayList<PostType> postTypeList, Pageable pageable);

	List<Post> findByUploadedAtLessThanAndTypeInOrderByUploadedAtDesc(OffsetDateTime uploadedAt,
			ArrayList<PostType> postTypeList, Pageable pageable);
	
}
