package com.ii.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.PasswordHistory;

public interface IPasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {

}
