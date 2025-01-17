package com.ii.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.BroadcastHistory;

public interface IBroadcastHistoryRepository extends JpaRepository<BroadcastHistory, UUID> {

}
