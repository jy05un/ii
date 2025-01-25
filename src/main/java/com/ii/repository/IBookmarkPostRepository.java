package com.ii.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.BookmarkPost;

public interface IBookmarkPostRepository extends JpaRepository<BookmarkPost, UUID> {
	
	List<BookmarkPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
	List<BookmarkPost> findByCreatedAtLessThanOrderByCreatedAtDesc(OffsetDateTime createdAt, Pageable pageable);
	Optional<BookmarkPost> findByPostId(UUID cursor);
	Optional<BookmarkPost> findByBookmarkIdAndPostId(UUID bookmarkId, UUID postId);

}
