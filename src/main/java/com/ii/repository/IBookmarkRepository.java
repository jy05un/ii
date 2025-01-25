package com.ii.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.Bookmark;

public interface IBookmarkRepository extends JpaRepository<Bookmark, UUID> {
	
}
