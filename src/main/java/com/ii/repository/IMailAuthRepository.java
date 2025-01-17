package com.ii.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.MailAuth;
import com.ii.object.entity.User;

public interface IMailAuthRepository extends JpaRepository<MailAuth, UUID> {

	MailAuth findByAuthCode(UUID authCode);

	MailAuth findByUser(User user);

}
