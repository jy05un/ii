package com.ii.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.User;

public interface IUserRepository extends JpaRepository<User, UUID>{

	User findByUsername(String username);

}
