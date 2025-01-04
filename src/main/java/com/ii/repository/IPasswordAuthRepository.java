package com.ii.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.PasswordAuth;

public interface IPasswordAuthRepository extends JpaRepository<PasswordAuth, UUID>{

	PasswordAuth findByAuthCode(UUID authCode);

}
