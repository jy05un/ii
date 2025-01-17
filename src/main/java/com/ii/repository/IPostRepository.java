package com.ii.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.Post;

public interface IPostRepository extends JpaRepository<Post, UUID> {

}
