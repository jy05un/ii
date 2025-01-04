package com.ii.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.User;

public interface IUserRepository extends JpaRepository<User, UUID>{

	User findByUsername(String username);

	Boolean existsByOAuth2IdAndOAuth2Type(String oAuth2Id, String oAuth2Type);

	User findByOAuth2IdAndOAuth2Type(String oAuth2Id, String oAuth2Type);

	Boolean existsByUsername(String username);

	void deleteByUsername(String username);

	User findByEmail(String email);

}
